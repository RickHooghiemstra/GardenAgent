package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.WeatherReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherReadingDao {
    @Query("SELECT * FROM weather_readings ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestReading(): Flow<WeatherReadingEntity?>

    @Query("SELECT * FROM weather_readings ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecentReadings(limit: Int): Flow<List<WeatherReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: WeatherReadingEntity)
}
