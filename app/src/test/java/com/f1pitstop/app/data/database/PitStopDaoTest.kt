package com.f1pitstop.app.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.f1pitstop.app.data.database.PitStopDatabase
import com.f1pitstop.app.data.model.EstadoPitStop
import com.f1pitstop.app.data.model.Escuderia
import com.f1pitstop.app.utils.PitStopFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Tests unitarios para PitStopDao
 */
@RunWith(RobolectricTestRunner::class)
class PitStopDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: PitStopDatabase
    private lateinit var dao: PitStopDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            PitStopDatabase::class.java
        ).allowMainThreadQueries().build()
        
        dao = database.pitStopDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertPitStop_returnsCorrectId() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        
        // When
        val id = dao.insertPitStop(pitStop)
        
        // Then
        assertThat(id).isGreaterThan(0)
    }
    
    @Test
    fun getPitStopById_returnsCorrectPitStop() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop("Lewis Hamilton")
        val id = dao.insertPitStop(pitStop)
        
        // When
        val retrievedPitStop = dao.getPitStopById(id)
        
        // Then
        assertThat(retrievedPitStop).isNotNull()
        assertThat(retrievedPitStop?.piloto).isEqualTo("Lewis Hamilton")
        assertThat(retrievedPitStop?.id).isEqualTo(id)
    }
    
    @Test
    fun getPitStopById_withInvalidId_returnsNull() = runTest {
        // When
        val retrievedPitStop = dao.getPitStopById(999L)
        
        // Then
        assertThat(retrievedPitStop).isNull()
    }
    
    @Test
    fun getAllPitStops_returnsAllPitStopsOrderedByDate() = runTest {
        // Given
        val pitStop1 = PitStopFactory.createSpecificPitStop("Pilot 1").copy(fechaHora = 1000L)
        val pitStop2 = PitStopFactory.createSpecificPitStop("Pilot 2").copy(fechaHora = 2000L)
        val pitStop3 = PitStopFactory.createSpecificPitStop("Pilot 3").copy(fechaHora = 1500L)
        
        dao.insertPitStop(pitStop1)
        dao.insertPitStop(pitStop2)
        dao.insertPitStop(pitStop3)
        
        // When
        val allPitStops = dao.getAllPitStops().first()
        
        // Then
        assertThat(allPitStops).hasSize(3)
        // Debe estar ordenado por fecha descendente
        assertThat(allPitStops[0].piloto).isEqualTo("Pilot 2") // fechaHora = 2000L
        assertThat(allPitStops[1].piloto).isEqualTo("Pilot 3") // fechaHora = 1500L
        assertThat(allPitStops[2].piloto).isEqualTo("Pilot 1") // fechaHora = 1000L
    }
    
    @Test
    fun updatePitStop_updatesCorrectly() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop("Original Pilot")
        val id = dao.insertPitStop(pitStop)
        val updatedPitStop = pitStop.copy(id = id, piloto = "Updated Pilot")
        
        // When
        dao.updatePitStop(updatedPitStop)
        val retrievedPitStop = dao.getPitStopById(id)
        
        // Then
        assertThat(retrievedPitStop?.piloto).isEqualTo("Updated Pilot")
    }
    
    @Test
    fun deletePitStop_removesFromDatabase() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        val id = dao.insertPitStop(pitStop)
        val insertedPitStop = dao.getPitStopById(id)!!
        
        // When
        dao.deletePitStop(insertedPitStop)
        val retrievedPitStop = dao.getPitStopById(id)
        
        // Then
        assertThat(retrievedPitStop).isNull()
    }
    
    @Test
    fun deletePitStopById_removesFromDatabase() = runTest {
        // Given
        val pitStop = PitStopFactory.createSpecificPitStop()
        val id = dao.insertPitStop(pitStop)
        
        // When
        dao.deletePitStopById(id)
        val retrievedPitStop = dao.getPitStopById(id)
        
        // Then
        assertThat(retrievedPitStop).isNull()
    }
    
    @Test
    fun searchPitStops_byPiloto_returnsMatchingResults() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Lewis Hamilton"))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Max Verstappen"))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Charles Leclerc"))
        
        // When
        val searchResults = dao.searchPitStops("Lewis").first()
        
        // Then
        assertThat(searchResults).hasSize(1)
        assertThat(searchResults[0].piloto).isEqualTo("Lewis Hamilton")
    }
    
    @Test
    fun searchPitStops_byEscuderia_returnsMatchingResults() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Pilot 1", Escuderia.MERCEDES))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Pilot 2", Escuderia.FERRARI))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop("Pilot 3", Escuderia.MERCEDES))
        
        // When
        val searchResults = dao.searchPitStops("Mercedes").first()
        
        // Then
        assertThat(searchResults).hasSize(2)
        searchResults.forEach { pitStop ->
            assertThat(pitStop.escuderia).isEqualTo(Escuderia.MERCEDES)
        }
    }
    
    @Test
    fun getFastestTime_returnsMinimumTimeFromSuccessfulPitStops() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 2.5, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 1.8, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 3.2, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 1.5, estado = EstadoPitStop.FALLIDO)) // No debe contar
        
        // When
        val fastestTime = dao.getFastestTime()
        
        // Then
        assertThat(fastestTime).isEqualTo(1.8)
    }
    
    @Test
    fun getAverageTime_returnsCorrectAverageFromSuccessfulPitStops() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 2.0, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 3.0, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 4.0, estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(tiempo = 10.0, estado = EstadoPitStop.FALLIDO)) // No debe contar
        
        // When
        val averageTime = dao.getAverageTime()
        
        // Then
        assertThat(averageTime).isEqualTo(3.0) // (2.0 + 3.0 + 4.0) / 3 = 3.0
    }
    
    @Test
    fun getTotalCount_returnsCorrectCount() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        
        // When
        val totalCount = dao.getTotalCount()
        
        // Then
        assertThat(totalCount).isEqualTo(3)
    }
    
    @Test
    fun getLastFivePitStops_returnsCorrectNumberAndOrder() = runTest {
        // Given - Insertar 7 pit stops con fechas diferentes
        for (i in 1..7) {
            dao.insertPitStop(
                PitStopFactory.createSpecificPitStop("Pilot $i")
                    .copy(fechaHora = i * 1000L)
            )
        }
        
        // When
        val lastFive = dao.getLastFivePitStops()
        
        // Then
        assertThat(lastFive).hasSize(5)
        // Debe estar ordenado por fecha descendente
        assertThat(lastFive[0].piloto).isEqualTo("Pilot 7")
        assertThat(lastFive[4].piloto).isEqualTo("Pilot 3")
    }
    
    @Test
    fun getPitStopsByEscuderia_returnsOnlyMatchingEscuderia() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(escuderia = Escuderia.MERCEDES))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(escuderia = Escuderia.FERRARI))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(escuderia = Escuderia.MERCEDES))
        
        // When
        val mercedesPitStops = dao.getPitStopsByEscuderia("Mercedes").first()
        
        // Then
        assertThat(mercedesPitStops).hasSize(2)
        mercedesPitStops.forEach { pitStop ->
            assertThat(pitStop.escuderia).isEqualTo(Escuderia.MERCEDES)
        }
    }
    
    @Test
    fun getPitStopsByEstado_returnsOnlyMatchingEstado() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(estado = EstadoPitStop.OK))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(estado = EstadoPitStop.FALLIDO))
        dao.insertPitStop(PitStopFactory.createSpecificPitStop(estado = EstadoPitStop.OK))
        
        // When
        val okPitStops = dao.getPitStopsByEstado("OK").first()
        
        // Then
        assertThat(okPitStops).hasSize(2)
        okPitStops.forEach { pitStop ->
            assertThat(pitStop.estado).isEqualTo(EstadoPitStop.OK)
        }
    }
    
    @Test
    fun deleteAllPitStops_removesAllRecords() = runTest {
        // Given
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        dao.insertPitStop(PitStopFactory.createSpecificPitStop())
        
        // When
        dao.deleteAllPitStops()
        val allPitStops = dao.getAllPitStops().first()
        
        // Then
        assertThat(allPitStops).isEmpty()
    }
}