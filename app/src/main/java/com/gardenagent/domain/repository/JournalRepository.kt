package com.gardenagent.domain.repository

import com.gardenagent.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    fun getEntriesForPlant(plantId: Long): Flow<List<JournalEntry>>
    fun getRecentEntries(limit: Int = 10): Flow<List<JournalEntry>>
    suspend fun insertEntry(entry: JournalEntry): Long
    suspend fun deleteEntry(entry: JournalEntry)
}
