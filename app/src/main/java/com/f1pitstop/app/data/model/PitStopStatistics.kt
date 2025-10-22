package com.f1pitstop.app.data.model

/**
 * Estadísticas de pit stops
 */
data class PitStopStatistics(
    val totalCount: Int,
    val fastestTime: Double?,
    val averageTime: Double?,
    val lastFivePitStops: List<PitStop>
) {
    companion object {
        /**
         * Crea estadísticas vacías
         * @return PitStopStatistics con valores por defecto
         */
        fun empty(): PitStopStatistics {
            return PitStopStatistics(
                totalCount = 0,
                fastestTime = null,
                averageTime = null,
                lastFivePitStops = emptyList()
            )
        }
    }
}