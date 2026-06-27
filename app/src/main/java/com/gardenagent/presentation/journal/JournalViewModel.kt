package com.gardenagent.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.usecase.journal.DeleteJournalEntryUseCase
import com.gardenagent.domain.usecase.journal.GetJournalEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase,
) : ViewModel() {
    val entries: StateFlow<List<JournalEntry>> = getJournalEntriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch { deleteJournalEntryUseCase(entry) }
    }
}
