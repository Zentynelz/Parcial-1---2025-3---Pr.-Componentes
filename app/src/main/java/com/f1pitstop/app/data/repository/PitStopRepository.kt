package com.f1pitstop.app.data.repository

import com.f1pitstop.app.data.database.PitStopDao
import com.f1pitstop.app.data.exception.PitStopException
import com.f1pitstop.app.data.model.EstadoPitStop
import com.f1pitstop.app.data.model.PitStop

import com.f1pitstop.app.data.model.PitStopStatistics
import com.f1pitstop.app.data.model.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * Repository que maneja todas las operaciones de datos para PitStop
 * Implementa el patrón Repository para abstraer el acceso a datos
 */
class PitStopRepository(private val pitStopDao: PitStopDao) {

    /**
     * Obtiene todos los pit stops como Flow
     * @return Flow con lista de pit stops ordenados por fecha descendente
     */
    fun getAllPitStops(): Flow<List<PitStop>> {
        return pitStopDao.getAllPitStops()
            .catch { exception ->
                throw PitStopException.DatabaseException("Error al obtener pit stops: ${exception.message}")
            }
    }

    /**
     * Obtiene un pit stop por su ID
     * @param id ID del pit stop
     * @return PitStop o null si no existe
     * @throws PitStopException.DatabaseException si hay error en la consulta
     */
    suspend fun getPitStopById(id: Long): PitStop? {
        return try {
            pitStopDao.getPitStopById(id)
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al obtener pit stop con ID $id: ${exception.message}")
        }
    }

    /**
     * Inserta un nuevo pit stop después de validarlo
     * @param pitStop PitStop a insertar
     * @return ID del pit stop insertado
     * @throws PitStopException.ValidationException si los datos no son válidos
     * @throws PitStopException.DatabaseException si hay error en la inserción
     */
    suspend fun insertPitStop(pitStop: PitStop): Long {
        val validationResult = validatePitStop(pitStop)
        if (!validationResult.isValid) {
            throw PitStopException.ValidationException(validationResult.getAllErrorsAsString())
        }

        return try {
            pitStopDao.insertPitStop(pitStop)
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al guardar pit stop: ${exception.message}")
        }
    }

    /**
     * Actualiza un pit stop existente después de validarlo
     * @param pitStop PitStop con datos actualizados
     * @throws PitStopException.ValidationException si los datos no son válidos
     * @throws PitStopException.DatabaseException si hay error en la actualización
     */
    suspend fun updatePitStop(pitStop: PitStop) {
        val validationResult = validatePitStop(pitStop)
        if (!validationResult.isValid) {
            throw PitStopException.ValidationException(validationResult.getAllErrorsAsString())
        }

        try {
            pitStopDao.updatePitStop(pitStop)
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al actualizar pit stop: ${exception.message}")
        }
    }
    /**
     * Obtiene un pit stop por su ID.
     * Método auxiliar para cumplir con la interfaz usada por la capa de presentación.
     */
    suspend fun getPitStop(id: Long): PitStop? {
        return getPitStopById(id)
    }

    /**
     * Inserta o actualiza un pit stop dependiendo de si tiene ID asignado.
     * @param pitStop Pit stop a insertar o actualizar.
     * @return ID del registro afectado.
     */
    suspend fun upsertPitStop(pitStop: PitStop): Long {
        val validationResult = validatePitStop(pitStop)
        if (!validationResult.isValid) {
            throw PitStopException.ValidationException(validationResult.getAllErrorsAsString())
        }

        return try {
            if (pitStop.id == 0L) {
                pitStopDao.insertPitStop(pitStop)
            } else {
                pitStopDao.updatePitStop(pitStop)
                pitStop.id
            }
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al guardar pit stop: ${exception.message}")
        }
    }
    /**
     * Elimina un pit stop
     * @param pitStop PitStop a eliminar
     * @throws PitStopException.DatabaseException si hay error en la eliminación
     */
    suspend fun deletePitStop(pitStop: PitStop) {
        try {
            pitStopDao.deletePitStop(pitStop)
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al eliminar pit stop: ${exception.message}")
        }
    }

    /**
     * Elimina un pit stop por su ID
     * @param id ID del pit stop a eliminar
     * @throws PitStopException.DatabaseException si hay error en la eliminación
     */
    suspend fun deletePitStopById(id: Long) {
        try {
            pitStopDao.deletePitStopById(id)
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al eliminar pit stop con ID $id: ${exception.message}")
        }
    }

    /**
     * Busca pit stops por texto
     * @param query Texto a buscar en piloto o escudería
     * @return Flow con pit stops que coinciden con la búsqueda
     */
    fun searchPitStops(query: String): Flow<List<PitStop>> {
        return pitStopDao.searchPitStops(query.trim())
            .catch { exception ->
                throw PitStopException.DatabaseException("Error en búsqueda: ${exception.message}")
            }
    }

    /**
     * Obtiene estadísticas de pit stops
     * @return PitStopStatistics con datos estadísticos
     * @throws PitStopException.DatabaseException si hay error al obtener estadísticas
     */
    suspend fun getStatistics(): PitStopStatistics {
        return try {
            // Por ahora retornamos estadísticas vacías
            // Las estadísticas reales se obtienen via Flow en el ViewModel
            PitStopStatistics.empty()
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al obtener estadísticas: ${exception.message}")
        }
    }
    
    /**
     * Obtiene el pit stop más rápido
     * @return Flow con el pit stop más rápido o null
     */
    fun getFastestPitStop(): Flow<PitStop?> {
        return pitStopDao.getFastestPitStop()
            .catch { exception ->
                throw PitStopException.DatabaseException("Error al obtener pit stop más rápido: ${exception.message}")
            }
    }
    
    /**
     * Obtiene el tiempo promedio de pit stops
     * @return Tiempo promedio o null
     */
    suspend fun getAveragePitStopTime(): Double? {
        return try {
            pitStopDao.getAverageTime()
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al obtener tiempo promedio: ${exception.message}")
        }
    }
    
    /**
     * Obtiene el total de pit stops
     * @return Número total de pit stops
     */
    suspend fun getTotalPitStopsCount(): Int {
        return try {
            pitStopDao.getTotalCount()
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al obtener total de pit stops: ${exception.message}")
        }
    }
    
    /**
     * Obtiene los últimos N pit stops
     * @param limit Número de pit stops a obtener
     * @return Flow con los últimos pit stops
     */
    fun getLastNPitStops(limit: Int): Flow<List<PitStop>> {
        return pitStopDao.getLastNPitStops(limit)
            .catch { exception ->
                throw PitStopException.DatabaseException("Error al obtener últimos pit stops: ${exception.message}")
            }
    }

    /**
     * Obtiene pit stops por escudería
     * @param escuderia Escudería
     * @return Flow con pit stops de la escudería
     */
    fun getPitStopsByEscuderia(escuderia: com.f1pitstop.app.data.model.Escuderia): Flow<List<PitStop>> {
        return pitStopDao.getPitStopsByEscuderia(escuderia.displayName)
            .catch { exception ->
                throw PitStopException.DatabaseException("Error al obtener pit stops por escudería: ${exception.message}")
            }
    }

    /**
     * Obtiene pit stops por estado
     * @param estado Estado del pit stop
     * @return Flow con pit stops del estado especificado
     */
    fun getPitStopsByEstado(estado: EstadoPitStop): Flow<List<PitStop>> {
        return pitStopDao.getPitStopsByEstado(estado.displayName)
            .catch { exception ->
                throw PitStopException.DatabaseException("Error al obtener pit stops por estado: ${exception.message}")
            }
    }

    /**
     * Valida los datos de un pit stop
     * @param pitStop PitStop a validar
     * @return ValidationResult con el resultado de la validación
     */
    fun validatePitStop(pitStop: PitStop): ValidationResult {
        val errors = mutableListOf<String>()

        // Validar piloto
        if (pitStop.piloto.isBlank()) {
            errors.add("El nombre del piloto no puede estar vacío")
        } else if (pitStop.piloto.length < 2) {
            errors.add("El nombre del piloto debe tener al menos 2 caracteres")
        } else if (pitStop.piloto.length > 50) {
            errors.add("El nombre del piloto no puede tener más de 50 caracteres")
        }

        // Validar tiempo total
        if (pitStop.tiempoTotal <= 0) {
            errors.add("El tiempo total debe ser mayor a 0 segundos")
        } else if (pitStop.tiempoTotal > 300) { // 5 minutos máximo
            errors.add("El tiempo total no puede ser mayor a 300 segundos")
        }

        // Validar número de neumáticos
        if (pitStop.numeroNeumaticosCambiados < 1 || pitStop.numeroNeumaticosCambiados > 4) {
            errors.add("El número de neumáticos cambiados debe estar entre 1 y 4")
        }

        // Validar mecánico principal
        if (pitStop.mecanicoPrincipal.isBlank()) {
            errors.add("El nombre del mecánico principal no puede estar vacío")
        } else if (pitStop.mecanicoPrincipal.length < 2) {
            errors.add("El nombre del mecánico principal debe tener al menos 2 caracteres")
        } else if (pitStop.mecanicoPrincipal.length > 50) {
            errors.add("El nombre del mecánico principal no puede tener más de 50 caracteres")
        }

        // Validar motivo de fallo si el estado es FALLIDO
        if (pitStop.estado == EstadoPitStop.FALLIDO) {
            if (pitStop.motivoFallo.isNullOrBlank()) {
                errors.add("Debe especificar el motivo del fallo cuando el estado es 'Fallido'")
            } else if (pitStop.motivoFallo.length > 200) {
                errors.add("El motivo del fallo no puede tener más de 200 caracteres")
            }
        }

        // Validar fecha
        if (pitStop.fechaHora > System.currentTimeMillis()) {
            errors.add("La fecha del pit stop no puede ser futura")
        }

        return if (errors.isEmpty()) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(errors)
        }
    }

    /**
     * Elimina todos los pit stops (útil para testing)
     * @throws PitStopException.DatabaseException si hay error
     */
    suspend fun deleteAllPitStops() {
        try {
            pitStopDao.deleteAllPitStops()
        } catch (exception: Exception) {
            throw PitStopException.DatabaseException("Error al eliminar todos los pit stops: ${exception.message}")
        }
    }
}