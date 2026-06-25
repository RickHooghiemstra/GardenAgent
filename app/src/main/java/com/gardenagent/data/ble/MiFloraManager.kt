package com.gardenagent.data.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.provider.SensorScanResult
import com.gardenagent.domain.provider.SoilSensorProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val MI_FLORA_SERVICE = UUID.fromString("0000fe95-0000-1000-8000-00805f9b34fb")
private val CHAR_MODE_SWITCH = UUID.fromString("00001a00-0000-1000-8000-00805f9b34fb")
private val CHAR_SENSOR_DATA = UUID.fromString("00001a01-0000-1000-8000-00805f9b34fb")
private val CHAR_BATTERY = UUID.fromString("00001a02-0000-1000-8000-00805f9b34fb")

@Singleton
class MiFloraManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SoilSensorProvider {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private var scanCallback: ScanCallback? = null

    override fun scan(): Flow<SensorScanResult> = callbackFlow {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
            ?: run { close(IllegalStateException("Bluetooth not available")); return@callbackFlow }

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(MI_FLORA_SERVICE))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(SensorScanResult(
                    address = result.device.address,
                    name = result.device.name,
                    rssi = result.rssi,
                ))
            }
        }

        scanner.startScan(listOf(filter), settings, scanCallback!!)
        awaitClose {
            scanner.stopScan(scanCallback!!)
            scanCallback = null
        }
    }

    override fun stopScan() {
        scanCallback?.let {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(it)
            scanCallback = null
        }
    }

    override suspend fun readSensor(address: String, name: String?): Result<SensorReading> =
        withTimeout(15_000) {
            runCatching { readSensorInternal(address, name) }
        }

    @Suppress("DEPRECATION")
    private suspend fun readSensorInternal(address: String, name: String?): SensorReading =
        suspendCancellableCoroutine { cont ->
            val device = bluetoothAdapter?.getRemoteDevice(address)
                ?: run { cont.resumeWithException(IllegalStateException("Device not found: $address")); return@suspendCancellableCoroutine }

            var gatt: BluetoothGatt? = null
            var sensorData: SensorRawData? = null
            var battery: Int? = null

            val callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        g.discoverServices()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        g.close()
                        if (!cont.isCompleted) cont.resumeWithException(IllegalStateException("Disconnected before read"))
                    }
                }

                override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                    val service = g.getService(MI_FLORA_SERVICE)
                        ?: run { cont.resumeWithException(IllegalStateException("Mi Flora service not found")); return }
                    val modeChar = service.getCharacteristic(CHAR_MODE_SWITCH)
                    modeChar.value = byteArrayOf(0xa0.toByte(), 0x1f)
                    g.writeCharacteristic(modeChar)
                }

                override fun onCharacteristicWrite(g: BluetoothGatt, char: BluetoothGattCharacteristic, status: Int) {
                    if (char.uuid == CHAR_MODE_SWITCH) {
                        val dataChar = g.getService(MI_FLORA_SERVICE)?.getCharacteristic(CHAR_SENSOR_DATA)
                        if (dataChar != null) g.readCharacteristic(dataChar)
                        else cont.resumeWithException(IllegalStateException("Sensor data characteristic not found"))
                    }
                }

                override fun onCharacteristicRead(g: BluetoothGatt, char: BluetoothGattCharacteristic, status: Int) {
                    when (char.uuid) {
                        CHAR_SENSOR_DATA -> {
                            sensorData = MiFloraParser.parseSensorData(char.value)
                            val batteryChar = g.getService(MI_FLORA_SERVICE)?.getCharacteristic(CHAR_BATTERY)
                            if (batteryChar != null) g.readCharacteristic(batteryChar)
                            else finalize(g)
                        }
                        CHAR_BATTERY -> {
                            battery = MiFloraParser.parseBattery(char.value)
                            finalize(g)
                        }
                    }
                }

                private fun finalize(g: BluetoothGatt) {
                    g.disconnect()
                    val raw = sensorData
                    if (raw != null && !cont.isCompleted) {
                        cont.resume(SensorReading(
                            deviceAddress = address,
                            deviceName = name,
                            temperatureCelsius = raw.temperatureCelsius,
                            moisturePercent = raw.moisturePercent,
                            lightLux = raw.lightLux,
                            fertilityMicroSiemens = raw.fertilityMicroSiemens,
                            batteryPercent = battery ?: 0,
                        ))
                    } else if (!cont.isCompleted) {
                        cont.resumeWithException(IllegalStateException("Failed to read sensor data"))
                    }
                }
            }

            gatt = device.connectGatt(context, false, callback)
            cont.invokeOnCancellation { gatt?.close() }
        }
}
