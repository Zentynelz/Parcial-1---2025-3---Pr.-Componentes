package com.f1pitstop.app.data.database

import androidx.room.*
import com.f1pitstop.app.data.model.PitStop
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones de base de datos de PitStop
 */
@Dao
interface PitStopDao {
    
    /**
     * Obtiene todos los pit stops ordenados por fecha descendente
     * @return Flow con lista de pit stops
     */
    @Query("SELECT * FROM pit_stops ORDER BY fecha_hora DESC")
    fun getAllPitStops(): Flow<List<PitStop>>
    
    /**
     * Obtiene un pit stop por su ID
     * @param id ID del pit stop
     * @return PitStop o null si no existe
     */
    @Query("SELECT * FROM pit_stops WHERE id = :id")
    suspend fun getPitStopById(id: Long): PitStop?
    
    /**
     * Inserta un nuevo pit stop
     * @param pitStop PitStop a insertar
     * @return ID del pit stop insertado
     */
    @Insert
    suspend fun insertPitStop(pitStop: PitStop): Long
    
    /**
     * Actualiza un pit stop existente
     * @param pitStop PitStop con datos actualizados
     */
    @Update
    suspend fun updatePitStop(pitStop: PitStop)
    
    /**
     * Elimina un pit stop
     * @param pitStop PitStop a eliminar
     */
    @Delete
    suspend fun deletePitStop(pitStop: PitStop)
    
    /**
     * Elimina un pit stop por su ID
     * @param id ID del pit stop a eliminar
     */
    @Query("DELETE FROM pit_stops WHERE id = :id")
    suspend fun deletePitStopById(id: Long)
    
    /**
     * Busca pit stops por piloto o escudería
     * @param searchQuery Texto a buscar
     * @return Flow con pit stops que coinciden con la búsqueda
     */
    @Query("""
        SELECT * FROM pit_stops 
        WHERE piloto LIKE '%' || :searchQuery || '%' 
        OR escuderia LIKE '%' || :searchQuery || '%'
        ORDER BY fecha_hora DESC
    """)
    fun searchPitStops(searchQuery: String): Flow<List<PitStop>>
    
    /**
     * Obtiene el tiempo más rápido de pit stops exitosos
     * @return Tiempo más rápido o null si no hay pit stops exitosos
     */
    @Query("SELECT MIN(tiempo_total) FROM pit_stops WHERE estado = 'OK'")
    suspend fun getFastestTime(): Double?
    
    /**
     * Obtiene el pit stop más rápido
     * @return Pit stop más rápido o null si no hay pit stops exitosos
     */
    @Query("SELECT * FROM pit_stops WHERE estado = 'OK' ORDER BY tiempo_total ASC LIMIT 1")
    fun getFastestPitStop(): Flow<PitStop?>
    
    /**
     * Obtiene el promedio de tiempos de pit stops exitosos
     * @return Promedio de tiempos o null si no hay pit stops exitosos
     */
    @Query("SELECT AVG(tiempo_total) FROM pit_stops WHERE estado = 'OK'")
    fun getAverageTime(): Flow<Double?>
    
    /**
     * Obtiene el total de pit stops registrados
     * @return Número total de pit stops
     */
    @Query("SELECT COUNT(*) FROM pit_stops")
    fun getTotalCount(): Flow<Int>
    
    /**
     * Obtiene los últimos 5 pit stops para el gráfico
     * @return Lista con los últimos 5 pit stops
     */
    @Query("SELECT * FROM pit_stops ORDER BY fecha_hora DESC LIMIT 5")
    suspend fun getLastFivePitStops(): List<PitStop>
    
    /**
     * Obtiene los últimos N pit stops para el gráfico
     * @return Lista con los últimos N pit stops
     */
    @Query("SELECT * FROM pit_stops ORDER BY fecha_hora DESC LIMIT :limit")
    fun getLastNPitStops(limit: Int): Flow<List<PitStop>>
    
    /**
     * Obtiene pit stops por escudería
     * @param escuderia Nombre de la escudería
     * @return Flow con pit stops de la escudería
     */
    @Query("SELECT * FROM pit_stops WHERE escuderia = :escuderia ORDER BY fecha_hora DESC")
    fun getPitStopsByEscuderia(escuderia: String): Flow<List<PitStop>>
    
    /**
     * Obtiene pit stops por estado
     * @param estado Estado del pit stop (OK o Fallido)
     * @return Flow con pit stops del estado especificado
     */
    @Query("SELECT * FROM pit_stops WHERE estado = :estado ORDER BY fecha_hora DESC")
    fun getPitStopsByEstado(estado: String): Flow<List<PitStop>>
    
    /**
     * Elimina todos los pit stops (útil para testing)
     */
    @Query("DELETE FROM pit_stops")
    suspend fun deleteAllPitStops()
}