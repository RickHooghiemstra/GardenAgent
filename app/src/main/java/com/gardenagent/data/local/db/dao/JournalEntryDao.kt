package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY capturedAt DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE plantId = :plantId ORDER BY capturedAt DESC")
    fun getEntriesForPlant(plantId: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY capturedAt DESC LIMIT :limit")
    fun getRecentEntries(limit: Int): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)
}
