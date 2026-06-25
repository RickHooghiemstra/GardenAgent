package com.gardenagent.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gardenagent.data.local.db.dao.JournalEntryDao
import com.gardenagent.data.local.db.dao.PlantDao
import com.gardenagent.data.local.db.dao.SensorReadingDao
import com.gardenagent.data.local.db.dao.WeatherReadingDao
import com.gardenagent.data.local.db.entity.JournalEntryEntity
import com.gardenagent.data.local.db.entity.PlantEntity
import com.gardenagent.data.local.db.entity.SensorReadingEntity
import com.gardenagent.data.local.db.entity.WeatherReadingEntity

@Database(
    entities = [
        PlantEntity::class,
        JournalEntryEntity::class,
        SensorReadingEntity::class,
        WeatherReadingEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun weatherReadingDao(): WeatherReadingDao
}
