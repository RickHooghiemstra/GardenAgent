package com.gardenagent.di

import android.content.Context
import androidx.room.Room
import com.gardenagent.data.local.db.GardenDatabase
import com.gardenagent.data.local.db.dao.JournalEntryDao
import com.gardenagent.data.local.db.dao.PlantDao
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
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun providePlantDao(db: GardenDatabase): PlantDao = db.plantDao()
    @Provides fun provideJournalEntryDao(db: GardenDatabase): JournalEntryDao = db.journalEntryDao()
    @Provides fun provideSensorReadingDao(db: GardenDatabase): SensorReadingDao = db.sensorReadingDao()
    @Provides fun provideWeatherReadingDao(db: GardenDatabase): WeatherReadingDao = db.weatherReadingDao()
}
