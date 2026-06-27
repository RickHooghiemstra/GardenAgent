package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.SensorDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDeviceDao {
    @Query("SELECT * FROM sensor_devices")
    fun getAll(): Flow<List<SensorDeviceEntity>>

    @Query("SELECT * FROM sensor_devices WHERE address = :address")
    suspend fun getByAddress(address: String): SensorDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: SensorDeviceEntity)

    @Delete
    suspend fun delete(device: SensorDeviceEntity)
}
