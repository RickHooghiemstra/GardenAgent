package com.gardenagent.di

import android.content.Context
import androidx.room.Room
import com.gardenagent.data.local.db.GardenDatabase
import com.gardenagent.data.local.db.dao.GardenDao
import com.gardenagent.data.local.db.dao.JournalEntryDao
import com.gardenagent.data.local.db.dao.ManualCameraDao
import com.gardenagent.data.local.db.dao.PlantDao
import com.gardenagent.data.local.db.dao.SensorDeviceDao
import com.gardenagent.data.local.db.dao.SensorReadingDao
import com.gardenagent.data.local.db.dao.WeatherReadingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GardenDatabase =
        Room.databaseBuilder(context, GardenDatabase::class.java, "garden_agent.db")
            .addMigrations(GardenDatabase.MIGRATION_1_2)
            .build()

    @Provides @Singleton fun providePlantDao(db: GardenDatabase): PlantDao = db.plantDao()
    @Provides @Singleton fun provideJournalEntryDao(db: GardenDatabase): JournalEntryDao = db.journalEntryDao()
    @Provides @Singleton fun provideSensorReadingDao(db: GardenDatabase): SensorReadingDao = db.sensorReadingDao()
    @Provides @Singleton fun provideWeatherReadingDao(db: GardenDatabase): WeatherReadingDao = db.weatherReadingDao()
    @Provides @Singleton fun provideGardenDao(db: GardenDatabase): GardenDao = db.gardenDao()
    @Provides @Singleton fun provideManualCameraDao(db: GardenDatabase): ManualCameraDao = db.manualCameraDao()
    @Provides @Singleton fun provideSensorDeviceDao(db: GardenDatabase): SensorDeviceDao = db.sensorDeviceDao()
}
