package com.gardenagent.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.repository.PlantRepository
import com.gardenagent.domain.usecase.journal.DeleteJournalEntryUseCase
import com.gardenagent.domain.usecase.journal.GetJournalEntriesUseCase
import com.gardenagent.domain.usecase.plant.DeletePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant

    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries

    fun loadPlant(plantId: Long) {
        viewModelScope.launch {
            _plant.value = plantRepository.getPlantById(plantId)
            getJournalEntriesUseCase(plantId)
                .collect { _entries.value = it }
        }
    }

    fun deleteJournalEntry(entry: JournalEntry) {
        viewModelScope.launch { deleteJournalEntryUseCase(entry) }
    }

    fun deletePlant() {
        viewModelScope.launch { _plant.value?.let { deletePlantUseCase(it) } }
    }
}
