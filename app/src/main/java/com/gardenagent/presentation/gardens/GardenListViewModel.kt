package com.gardenagent.presentation.gardens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.usecase.garden.DeleteGardenUseCase
import com.gardenagent.domain.usecase.garden.GetGardensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GardenListViewModel @Inject constructor(
    getGardensUseCase: GetGardensUseCase,
    private val deleteGardenUseCase: DeleteGardenUseCase,
) : ViewModel() {

    val gardens: StateFlow<List<Garden>> = getGardensUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteGarden(garden: Garden) {
        viewModelScope.launch { deleteGardenUseCase(garden) }
    }
}
