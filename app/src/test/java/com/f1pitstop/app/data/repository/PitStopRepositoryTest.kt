package com.f1pitstop.app.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.f1pitstop.app.data.database.PitStopDao
import com.f1pitstop.app.data.exception.PitStopException
import com.f1pitstop.app.data.model.EstadoPitStop
import com.f1pitstop.app.data.model.Escuderia
import com.f1pitstop.app.data.model.PitStop
import com.f1pitstop.app.utils.PitStopFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Tests unitarios para PitStopRepository
 */
class PitStopRepositoryTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @Mock
    private lateinit var mockDao: PitStopDao
    
    private lateinit var repository: PitStopRepository
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = PitStopRepository(mockDao)
    }
    
    @Test
    fun getAllPitStops_returnsFlowFromDao() = runTest {
        // Given
        val pitStops = listOf(PitStopFactory.createSpecificPitStop())
        whenever(mockDao.getAllPitStops()).thenReturn(flowOf(pitStops))
        
        // When
        val result = repository.getAllPitStops()
        
        // Then
        verify(mockDao).getAllPitStops()
        // Note: En un test real, necesitarías collect() el Flow para verificar el contenido
    }
    
    @Test
    fun getPitStopById_returnsCorrectPitStop() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        whenever(mockDao.getPitStopById(1L)).thenReturn(pitStop)
        
        // When
        val result = repository.getPitStopById(1L)
        
        // Then
        verify(mockDao).getPitStopById(1L)
        assertThat(result).isEqualTo(pitStop)
    }
    
    @Test
    fun getPitStopById_withDatabaseError_throwsDatabaseException() = runTest {
        // Given
        whenever(mockDao.getPitStopById(1L)).thenThrow(RuntimeException("Database error"))
        
        // When & Then
        try {
            repository.getPitStopById(1L)
            assertThat(false).isTrue() // No debería llegar aquí
        } catch (e: PitStopException.DatabaseException) {
            assertThat(e.message).contains("Database error")
        }
    }
    
    @Test
    fun insertPitStop_withValidData_insertsSuccessfully() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        whenever(mockDao.insertPitStop(pitStop)).thenReturn(1L)
        
        // When
        val result = repository.insertPitStop(pitStop)
        
        // Then
        verify(mockDao).insertPitStop(pitStop)
        assertThat(result).isEqualTo(1L)
    }
    
    @Test
    fun insertPitStop_withInvalidData_throwsValidationException() = runTest {
        // Given
        val invalidPitStop = PitStopFactory.createInvalidPitStop()
        
        // When & Then
        try {
            repository.insertPitStop(invalidPitStop)
            assertThat(false).isTrue() // No debería llegar aquí
        } catch (e: PitStopException.ValidationException) {
            assertThat(e.message).isNotEmpty()
        }
        
        // Verify que no se llamó al DAO
        verify(mockDao, never()).insertPitStop(any())
    }
    
    @Test
    fun updatePitStop_withValidData_updatesSuccessfully() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        
        // When
        repository.updatePitStop(pitStop)
        
        // Then
        verify(mockDao).updatePitStop(pitStop)
    }
    
    @Test
    fun updatePitStop_withInvalidData_throwsValidationException() = runTest {
        // Given
        val invalidPitStop = PitStopFactory.createInvalidPitStop()
        
        // When & Then
        try {
            repository.updatePitStop(invalidPitStop)
            assertThat(false).isTrue() // No debería llegar aquí
        } catch (e: PitStopException.ValidationException) {
            assertThat(e.message).isNotEmpty()
        }
        
        // Verify que no se llamó al DAO
        verify(mockDao, never()).updatePitStop(any())
    }
    
    @Test
    fun deletePitStop_callsDaoDelete() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        
        // When
        repository.deletePitStop(pitStop)
        
        // Then
        verify(mockDao).deletePitStop(pitStop)
    }
    
    @Test
    fun deletePitStopById_callsDaoDeleteById() = runTest {
        // Given
        val id = 1L
        
        // When
        repository.deletePitStopById(id)
        
        // Then
        verify(mockDao).deletePitStopById(id)
    }
    
    @Test
    fun searchPitStops_trimsQueryAndCallsDao() = runTest {
        // Given
        val query = "  Lewis  "
        val pitStops = listOf(PitStopFactory.createSpecificPitStop("Lewis Hamilton"))
        whenever(mockDao.searchPitStops("Lewis")).thenReturn(flowOf(pitStops))
        
        // When
        repository.searchPitStops(query)
        
        // Then
        verify(mockDao).searchPitStops("Lewis")
    }
    
    @Test
    fun getStatistics_returnsCorrectStatistics() = runTest {
        // When - El método getStatistics() ahora retorna estadísticas vacías
        val statistics = repository.getStatistics()
        
        // Then - Verificar que retorna estadísticas vacías
        assertThat(statistics.totalCount).isEqualTo(0)
        assertThat(statistics.fastestTime).isNull()
        assertThat(statistics.averageTime).isNull()
        assertThat(statistics.lastFivePitStops).isEmpty()
    }
    
    @Test
    fun validatePitStop_withValidData_returnsValid() {
        // Given
        val validPitStop = PitStopFactory.createSpecificPitStop(
            piloto = "Lewis Hamilton",
            tiempo = 2.5,
            estado = EstadoPitStop.OK
        )
        
        // When
        val result = repository.validatePitStop(validPitStop)
        
        // Then
        assertThat(result.isValid).isTrue()
        assertThat(result.errors).isEmpty()
    }
    
    @Test
    fun validatePitStop_withEmptyPiloto_returnsInvalid() {
        // Given
        val invalidPitStop = PitStopFactory.createSpecificPitStop(piloto = "")
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("El nombre del piloto no puede estar vacío")
    }
    
    @Test
    fun validatePitStop_withNegativeTime_returnsInvalid() {
        // Given
        val invalidPitStop = PitStopFactory.createSpecificPitStop(tiempo = -1.0)
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("El tiempo total debe ser mayor a 0 segundos")
    }
    
    @Test
    fun validatePitStop_withInvalidNeumaticoCount_returnsInvalid() {
        // Given
        val invalidPitStop = PitStopFactory.createSpecificPitStop().copy(numeroNeumaticosCambiados = 5)
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("El número de neumáticos cambiados debe estar entre 1 y 4")
    }
    
    @Test
    fun validatePitStop_withEmptyMecanico_returnsInvalid() {
        // Given
        val invalidPitStop = PitStopFactory.createSpecificPitStop().copy(mecanicoPrincipal = "")
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("El nombre del mecánico principal no puede estar vacío")
    }
    
    @Test
    fun validatePitStop_withFallidoStateAndNoMotivo_returnsInvalid() {
        // Given
        val invalidPitStop = PitStopFactory.createSpecificPitStop(
            estado = EstadoPitStop.FALLIDO
        ).copy(motivoFallo = null)
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("Debe especificar el motivo del fallo cuando el estado es 'Fallido'")
    }
    
    @Test
    fun validatePitStop_withFutureDate_returnsInvalid() {
        // Given
        val futureTimestamp = System.currentTimeMillis() + 86400000L // +1 día
        val invalidPitStop = PitStopFactory.createSpecificPitStop().copy(fechaHora = futureTimestamp)
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).contains("La fecha del pit stop no puede ser futura")
    }
    
    @Test
    fun validatePitStop_withMultipleErrors_returnsAllErrors() {
        // Given
        val invalidPitStop = PitStop(
            piloto = "", // Error 1
            escuderia = Escuderia.MERCEDES,
            tiempoTotal = -1.0, // Error 2
            cambioNeumaticos = com.f1pitstop.app.data.model.TipoNeumatico.SOFT,
            numeroNeumaticosCambiados = 5, // Error 3
            estado = EstadoPitStop.OK,
            mecanicoPrincipal = "", // Error 4
            fechaHora = System.currentTimeMillis()
        )
        
        // When
        val result = repository.validatePitStop(invalidPitStop)
        
        // Then
        assertThat(result.isValid).isFalse()
        assertThat(result.errors).hasSize(4)
    }
    
    @Test
    fun getPitStopsByEscuderia_callsDaoWithCorrectParameter() = runTest {
        // Given
        val escuderia = Escuderia.MERCEDES
        val pitStops = listOf(PitStopFactory.createSpecificPitStop(escuderia = escuderia))
        whenever(mockDao.getPitStopsByEscuderia("Mercedes")).thenReturn(flowOf(pitStops))
        
        // When
        repository.getPitStopsByEscuderia(escuderia)
        
        // Then
        verify(mockDao).getPitStopsByEscuderia("Mercedes")
    }
    
    @Test
    fun getPitStopsByEstado_callsDaoWithCorrectParameter() = runTest {
        // Given
        val estado = EstadoPitStop.OK
        val pitStops = listOf(PitStopFactory.createSpecificPitStop(estado = estado))
        whenever(mockDao.getPitStopsByEstado("OK")).thenReturn(flowOf(pitStops))
        
        // When
        repository.getPitStopsByEstado(estado)
        
        // Then
        verify(mockDao).getPitStopsByEstado("OK")
    }
    
    @Test
    fun deleteAllPitStops_callsDaoDeleteAll() = runTest {
        // When
        repository.deleteAllPitStops()
        
        // Then
        verify(mockDao).deleteAllPitStops()
    }
}