package com.gardenagent.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.usecase.plant.DeletePlantUseCase
import com.gardenagent.domain.usecase.plant.GetPlantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    getPlantsUseCase: GetPlantsUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
) : ViewModel() {

    private val _gardenFilter = MutableStateFlow<Long?>(null)

    fun setGardenFilter(id: Long?) { _gardenFilter.value = id }

    val plants: StateFlow<List<Plant>> = combine(getPlantsUseCase(), _gardenFilter) { list, filter ->
        if (filter != null) list.filter { it.gardenId == filter } else list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deletePlant(plant: Plant) {
        viewModelScope.launch { deletePlantUseCase(plant) }
    }
}
