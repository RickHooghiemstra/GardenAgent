package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.JournalEntryDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(private val dao: JournalEntryDao) : JournalRepository {
    override fun getAllEntries(): Flow<List<JournalEntry>> = dao.getAllEntries().map { it.map { e -> e.toDomain() } }
    override fun getEntriesForPlant(plantId: Long): Flow<List<JournalEntry>> = dao.getEntriesForPlant(plantId).map { it.map { e -> e.toDomain() } }
    override fun getRecentEntries(limit: Int): Flow<List<JournalEntry>> = dao.getRecentEntries(limit).map { it.map { e -> e.toDomain() } }
    override suspend fun insertEntry(entry: JournalEntry): Long = dao.insertEntry(entry.toEntity())
    override suspend fun deleteEntry(entry: JournalEntry) = dao.deleteEntry(entry.toEntity())
}
