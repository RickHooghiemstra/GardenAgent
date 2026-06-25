package com.gardenagent.domain.usecase.journal

import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetJournalEntriesUseCase @Inject constructor(private val repository: JournalRepository) {
    operator fun invoke(plantId: Long? = null): Flow<List<JournalEntry>> =
        if (plantId != null) repository.getEntriesForPlant(plantId)
        else repository.getAllEntries()
}
