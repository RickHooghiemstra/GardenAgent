package com.gardenagent

import com.gardenagent.data.ble.MiFloraParser
import org.junit.Assert.*
import org.junit.Test

class MiFloraParserTest {

    @Test
    fun `parseSensorData returns null for short arrays`() {
        assertNull(MiFloraParser.parseSensorData(ByteArray(5)))
    }

    @Test
    fun `parseSensorData correctly decodes known payload`() {
        // Temperature 21.5°C = 0x00D7, moisture 35%, light 1234 lux = 0x04D2, fertility 200 = 0x00C8
        val data = ByteArray(16)
        data[0] = 0xD7.toByte()    // temp low byte
        data[1] = 0x00              // temp high byte → 215 / 10 = 21.5°C
        data[3] = 0xD2.toByte()    // light low
        data[4] = 0x04              // light high → 1234 lux
        data[7] = 35               // moisture 35%
        data[8] = 0xC8.toByte()    // fertility low
        data[9] = 0x00              // fertility high → 200 µS/cm

        val result = MiFloraParser.parseSensorData(data)
        assertNotNull(result)
        assertEquals(21.5f, result!!.temperatureCelsius, 0.01f)
        assertEquals(35, result.moisturePercent)
        assertEquals(1234, result.lightLux)
        assertEquals(200, result.fertilityMicroSiemens)
    }

    @Test
    fun `parseBattery returns null for empty array`() {
        assertNull(MiFloraParser.parseBattery(ByteArray(0)))
    }

    @Test
    fun `parseBattery returns first byte as unsigned int`() {
        assertEquals(87, MiFloraParser.parseBattery(byteArrayOf(87)))
        assertEquals(255, MiFloraParser.parseBattery(byteArrayOf(0xFF.toByte())))
    }
}
