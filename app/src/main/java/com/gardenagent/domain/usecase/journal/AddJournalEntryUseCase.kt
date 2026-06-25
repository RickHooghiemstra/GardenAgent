package com.gardenagent.domain.usecase.journal

import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.repository.JournalRepository
import javax.inject.Inject

class AddJournalEntryUseCase @Inject constructor(private val repository: JournalRepository) {
    suspend operator fun invoke(entry: JournalEntry): Long = repository.insertEntry(entry)
}
