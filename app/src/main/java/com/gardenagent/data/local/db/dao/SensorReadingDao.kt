package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.SensorReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorReadingDao {
    @Query("SELECT * FROM sensor_readings ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecentReadings(limit: Int): Flow<List<SensorReadingEntity>>

    @Query("SELECT * FROM sensor_readings WHERE deviceAddress = :address ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestReadingForDevice(address: String): Flow<SensorReadingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorReadingEntity)

    @Query("DELETE FROM sensor_readings WHERE recordedAt < :cutoffMs")
    suspend fun deleteOldReadings(cutoffMs: Long)

    @Query("SELECT * FROM sensor_readings WHERE id IN (SELECT MAX(id) FROM sensor_readings GROUP BY deviceAddress)")
    suspend fun getLatestPerDevice(): List<SensorReadingEntity>
}
