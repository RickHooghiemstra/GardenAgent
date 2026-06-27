package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.GardenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {
    @Query("SELECT * FROM gardens ORDER BY createdAt ASC")
    fun getAll(): Flow<List<GardenEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(garden: GardenEntity): Long

    @Delete
    suspend fun delete(garden: GardenEntity)

    @Query("SELECT * FROM gardens WHERE id = :id")
    suspend fun getById(id: Long): GardenEntity?
}
