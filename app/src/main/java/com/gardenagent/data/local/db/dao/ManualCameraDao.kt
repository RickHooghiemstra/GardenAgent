package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.ManualCameraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ManualCameraDao {
    @Query("SELECT * FROM manual_cameras")
    fun getAll(): Flow<List<ManualCameraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(camera: ManualCameraEntity): Long

    @Delete
    suspend fun delete(camera: ManualCameraEntity)
}
