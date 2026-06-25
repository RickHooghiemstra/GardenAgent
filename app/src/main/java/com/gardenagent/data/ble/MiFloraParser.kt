package com.gardenagent.data.ble

object MiFloraParser {
    fun parseSensorData(data: ByteArray): SensorRawData? {
        if (data.size < 10) return null
        val temperature = ((data[1].toInt() and 0xFF) shl 8 or (data[0].toInt() and 0xFF)) / 10f
        val light = (data[4].toInt() and 0xFF) shl 8 or (data[3].toInt() and 0xFF)
        val moisture = data[7].toInt() and 0xFF
        val fertility = (data[9].toInt() and 0xFF) shl 8 or (data[8].toInt() and 0xFF)
        return SensorRawData(temperature, light, moisture, fertility)
    }

    fun parseBattery(data: ByteArray): Int? {
        if (data.isEmpty()) return null
        return data[0].toInt() and 0xFF
    }
}

data class SensorRawData(
    val temperatureCelsius: Float,
    val lightLux: Int,
    val moisturePercent: Int,
    val fertilityMicroSiemens: Int,
)
