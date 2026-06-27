package com.gardenagent.di

import com.gardenagent.data.repository.EufyRepositoryImpl
import com.gardenagent.data.repository.MaintenanceTaskRepositoryImpl
import com.gardenagent.data.repository.GardenAdviceRepositoryImpl
import com.gardenagent.data.repository.GardenRepositoryImpl
import com.gardenagent.data.repository.JournalRepositoryImpl
import com.gardenagent.data.repository.ManualCameraRepositoryImpl
import com.gardenagent.data.repository.PlantIdentificationRepositoryImpl
import com.gardenagent.data.repository.PlantRepositoryImpl
import com.gardenagent.data.repository.SensorDeviceRepositoryImpl
import com.gardenagent.data.repository.SensorRepositoryImpl
import com.gardenagent.data.repository.WeatherRepositoryImpl
import com.gardenagent.domain.repository.EufyRepository
import com.gardenagent.domain.repository.MaintenanceTaskRepository
import com.gardenagent.domain.repository.GardenAdviceRepository
import com.gardenagent.domain.repository.GardenRepository
import com.gardenagent.domain.repository.JournalRepository
import com.gardenagent.domain.repository.ManualCameraRepository
import com.gardenagent.domain.repository.PlantIdentificationRepository
import com.gardenagent.domain.repository.PlantRepository
import com.gardenagent.domain.repository.SensorDeviceRepository
import com.gardenagent.domain.repository.SensorRepository
import com.gardenagent.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindPlantRepository(impl: PlantRepositoryImpl): PlantRepository

    @Binds @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    @Binds @Singleton
    abstract fun bindSensorRepository(impl: SensorRepositoryImpl): SensorRepository

    @Binds @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds @Singleton
    abstract fun bindEufyRepository(impl: EufyRepositoryImpl): EufyRepository

    @Binds @Singleton
    abstract fun bindPlantIdentificationRepository(impl: PlantIdentificationRepositoryImpl): PlantIdentificationRepository

    @Binds @Singleton
    abstract fun bindGardenAdviceRepository(impl: GardenAdviceRepositoryImpl): GardenAdviceRepository

    @Binds @Singleton
    abstract fun bindGardenRepository(impl: GardenRepositoryImpl): GardenRepository

    @Binds @Singleton
    abstract fun bindManualCameraRepository(impl: ManualCameraRepositoryImpl): ManualCameraRepository

    @Binds @Singleton
    abstract fun bindSensorDeviceRepository(impl: SensorDeviceRepositoryImpl): SensorDeviceRepository

    @Binds @Singleton
    abstract fun bindMaintenanceTaskRepository(impl: MaintenanceTaskRepositoryImpl): MaintenanceTaskRepository
}
