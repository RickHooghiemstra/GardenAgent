package com.gardenagent.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.usecase.plant.DeletePlantUseCase
import com.gardenagent.domain.usecase.plant.GetPlantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    getPlantsUseCase: GetPlantsUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
) : ViewModel() {

    val plants: StateFlow<List<Plant>> = getPlantsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deletePlant(plant: Plant) {
        viewModelScope.launch { deletePlantUseCase(plant) }
    }
}
