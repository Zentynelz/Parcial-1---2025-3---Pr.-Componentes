package com.f1pitstop.app.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.f1pitstop.app.data.model.PitStop
import com.f1pitstop.app.data.repository.PitStopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de resumen de pit stops.
 * Proporciona los datos y la lógica de negocio para la UI de resumen,
 * incluyendo estadísticas y los últimos pit stops para el gráfico.
 */
class SummaryViewModel(private val repository: PitStopRepository) : ViewModel() {

    private val _fastestPitStop = MutableStateFlow<PitStop?>(null)
    val fastestPitStop: StateFlow<PitStop?> = _fastestPitStop.asStateFlow()

    private val _averagePitStopTime = MutableStateFlow<Double?>(null)
    val averagePitStopTime: StateFlow<Double?> = _averagePitStopTime.asStateFlow()

    private val _totalPitStopsCount = MutableStateFlow(0)
    val totalPitStopsCount: StateFlow<Int> = _totalPitStopsCount.asStateFlow()

    private val _lastPitStopsForChart = MutableStateFlow<List<PitStop>>(emptyList())
    val lastPitStopsForChart: StateFlow<List<PitStop>> = _lastPitStopsForChart.asStateFlow()

    init {
        collectPitStopData()
    }

    private fun collectPitStopData() {
        viewModelScope.launch {
            repository.getFastestPitStop().collect { _fastestPitStop.value = it }
        }
        viewModelScope.launch {
            _averagePitStopTime.value = repository.getAveragePitStopTime()
        }
        viewModelScope.launch {
            _totalPitStopsCount.value = repository.getTotalPitStopsCount()
        }
        viewModelScope.launch {
            repository.getLastNPitStops(5).collect { _lastPitStopsForChart.value = it }
        }
    }

    /**
     * Factory para crear instancias de SummaryViewModel.
     * Necesario para inyectar el repositorio en el ViewModel.
     */
    class Factory(private val repository: PitStopRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SummaryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}