package com.gardenagent.di

import com.gardenagent.data.ble.MiFloraManager
import com.gardenagent.data.remote.eufy.EufyCameraProvider
import com.gardenagent.domain.provider.CameraProvider
import com.gardenagent.domain.provider.SoilSensorProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvidersModule {

    @Binds @IntoSet @Singleton
    abstract fun bindMiFloraProvider(impl: MiFloraManager): SoilSensorProvider

    @Binds @IntoSet @Singleton
    abstract fun bindEufyCameraProvider(impl: EufyCameraProvider): CameraProvider
}
