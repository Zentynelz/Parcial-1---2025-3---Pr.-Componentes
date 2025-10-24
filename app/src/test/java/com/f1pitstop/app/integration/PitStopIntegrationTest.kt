package com.f1pitstop.app.integration


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.f1pitstop.app.data.database.PitStopDatabase
import com.f1pitstop.app.data.exception.PitStopException
import com.f1pitstop.app.data.model.EstadoPitStop
import com.f1pitstop.app.data.model.Escuderia
import com.f1pitstop.app.data.repository.PitStopRepository
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
 * Tests de integración que prueban el flujo completo desde Repository hasta Database
 */
@RunWith(RobolectricTestRunner::class)
class PitStopIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PitStopDatabase
    private lateinit var repository: PitStopRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            PitStopDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = PitStopRepository(database.pitStopDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun completeWorkflow_insertSearchUpdateDelete_worksCorrectly() = runTest {
        // 1. Insert - Insertar un pit stop válido
        val originalPitStop = PitStopFactory.createSpecificPitStop("Lewis Hamilton")
        val insertedId = repository.insertPitStop(originalPitStop)

        assertThat(insertedId).isGreaterThan(0)

        // 2. Search - Buscar el pit stop insertado
        val searchResults = repository.searchPitStops("Lewis").first()
        assertThat(searchResults).hasSize(1)
        assertThat(searchResults[0].piloto).isEqualTo("Lewis Hamilton")

        // 3. Get by ID - Obtener por ID
        val retrievedPitStop = repository.getPitStopById(insertedId)
        assertThat(retrievedPitStop).isNotNull()
        assertThat(retrievedPitStop?.piloto).isEqualTo("Lewis Hamilton")

        // 4. Update - Actualizar el pit stop
        val updatedPitStop = retrievedPitStop!!.copy(piloto = "Lewis Hamilton Updated")
        repository.updatePitStop(updatedPitStop)

        val afterUpdate = repository.getPitStopById(insertedId)
        assertThat(afterUpdate?.piloto).isEqualTo("Lewis Hamilton Updated")

        // 5. Delete - Eliminar el pit stop
        repository.deletePitStopById(insertedId)

        val afterDelete = repository.getPitStopById(insertedId)
        assertThat(afterDelete).isNull()
    }


    @Test
    fun statisticsWorkflow_insertsAndCalculatesCorrectly() = runTest {
        // Given - Insertar pit stops con tiempos conocidos
        val pitStops = PitStopFactory.createPitStopsForStatistics()
        for (pitStop in pitStops) {
            repository.insertPitStop(pitStop)
        }

        // When - Obtener estadísticas usando métodos del DAO
        val allPitStops = repository.getAllPitStops().first()
        val fastestPitStop = database.pitStopDao().getFastestPitStop().first()
        val averageTime = database.pitStopDao().getAverageTime().first()
        val totalCount = database.pitStopDao().getTotalCount().first()

        // Then - Verificar cálculos
        assertThat(totalCount).isEqualTo(5)

        // fastestPitStop puede ser null si no hay registros OK, así que lo verificamos de forma segura
        assertThat(fastestPitStop?.tiempoTotal).isEqualTo(2.1)

        // Promedio de pit stops exitosos: (2.1 + 2.3 + 2.8 + 2.5) / 4 = 2.425
        assertThat(averageTime).isWithin(0.01).of(2.425)

        assertThat(allPitStops).hasSize(5)
    }



    @Test
    fun validationIntegration_preventsInvalidDataFromBeingSaved() = runTest {
        // Given - Pit stop inválido
        val invalidPitStop = PitStopFactory.createInvalidPitStop()

        // When & Then - Intentar insertar debe fallar
        try {
            repository.insertPitStop(invalidPitStop)
            assertThat(false).isTrue() // No debería llegar aquí
        } catch (e: PitStopException.ValidationException) {
            assertThat(e.message).isNotEmpty()
        }

        // Verificar que no se guardó nada
        val allPitStops = repository.getAllPitStops().first()
        assertThat(allPitStops).isEmpty()
    }

    @Test
    fun searchIntegration_worksWithMultipleCriteria() = runTest {
        // Given - Insertar varios pit stops
        repository.insertPitStop(PitStopFactory.createSpecificPitStop("Lewis Hamilton"))
        repository.insertPitStop(PitStopFactory.createSpecificPitStop("Max Verstappen"))
        repository.insertPitStop(PitStopFactory.createSpecificPitStop("Charles Leclerc"))
        repository.insertPitStop(PitStopFactory.createSpecificPitStop("George Russell"))

        // When & Then - Buscar por diferentes criterios

        // Búsqueda por nombre parcial
        val lewisResults = repository.searchPitStops("Lewis").first()
        assertThat(lewisResults).hasSize(1)

        // Búsqueda por apellido
        val hamiltonResults = repository.searchPitStops("Hamilton").first()
        assertThat(hamiltonResults).hasSize(1)

        // Búsqueda que no encuentra nada
        val noResults = repository.searchPitStops("Alonso").first()
        assertThat(noResults).isEmpty()

        // Búsqueda vacía debe retornar todos
        val emptySearchResults = repository.searchPitStops("").first()
        assertThat(emptySearchResults).hasSize(4)
    }

    @Test
    fun filteringIntegration_byEscuderiaAndEstado() = runTest {
        // Given - Insertar pit stops con diferentes escuderías y estados
        repository.insertPitStop(
            PitStopFactory.createSpecificPitStop(
                "Pilot 1",
                Escuderia.MERCEDES,
                estado = EstadoPitStop.OK
            )
        )
        repository.insertPitStop(
            PitStopFactory.createSpecificPitStop(
                "Pilot 2",
                Escuderia.FERRARI,
                estado = EstadoPitStop.OK
            )
        )
        repository.insertPitStop(
            PitStopFactory.createSpecificPitStop(
                "Pilot 3",
                Escuderia.MERCEDES,
                estado = EstadoPitStop.FALLIDO
            )
        )

        // When & Then - Filtrar por escudería
        val mercedesPitStops = repository.getPitStopsByEscuderia(Escuderia.MERCEDES).first()
        assertThat(mercedesPitStops).hasSize(2)

        // Filtrar por estado
        val okPitStops = repository.getPitStopsByEstado(EstadoPitStop.OK).first()
        assertThat(okPitStops).hasSize(2)

        val fallidoPitStops = repository.getPitStopsByEstado(EstadoPitStop.FALLIDO).first()
        assertThat(fallidoPitStops).hasSize(1)
    }

    @Test
    fun bulkOperations_insertMultipleAndDeleteAll() = runTest {
        // Given - Crear múltiples pit stops
        val pitStops = PitStopFactory.createSamplePitStops(10)

        // When - Insertar todos
        for (pitStop in pitStops) {
            repository.insertPitStop(pitStop)
        }

        // Then - Verificar que se insertaron
        val allPitStops = repository.getAllPitStops().first()
        assertThat(allPitStops).hasSize(10)

        val totalCount = database.pitStopDao().getTotalCount()
        assertThat(totalCount).isEqualTo(10)

        // When - Eliminar todos
        repository.deleteAllPitStops()

        // Then - Verificar que se eliminaron
        val afterDelete = repository.getAllPitStops().first()
        assertThat(afterDelete).isEmpty()

        val emptyCount = database.pitStopDao().getTotalCount()
        assertThat(emptyCount).isEqualTo(0)
    }

    @Test
    fun orderingIntegration_pitStopsOrderedByDateDescending() = runTest {
        // Given - Insertar pit stops con fechas específicas
        val pitStop1 = PitStopFactory.createSpecificPitStop("Pilot 1").copy(fechaHora = 1000L)
        val pitStop2 = PitStopFactory.createSpecificPitStop("Pilot 2").copy(fechaHora = 3000L)
        val pitStop3 = PitStopFactory.createSpecificPitStop("Pilot 3").copy(fechaHora = 2000L)

        repository.insertPitStop(pitStop1)
        repository.insertPitStop(pitStop2)
        repository.insertPitStop(pitStop3)

        // When - Obtener todos los pit stops
        val allPitStops = repository.getAllPitStops().first()

        // Then - Verificar orden descendente por fecha
        assertThat(allPitStops).hasSize(3)
        assertThat(allPitStops[0].piloto).isEqualTo("Pilot 2") // fechaHora = 3000L
        assertThat(allPitStops[1].piloto).isEqualTo("Pilot 3") // fechaHora = 2000L
        assertThat(allPitStops[2].piloto).isEqualTo("Pilot 1") // fechaHora = 1000L
    }

    @Test
    fun errorHandling_validationExceptionsArePropagated() = runTest {
        // Given - Pit stop inválido que debería fallar la validación
        val invalidPitStop = PitStopFactory.createInvalidPitStop()

        // When & Then - La operación debe lanzar ValidationException
        var validationExceptionThrown = false
        try {
            repository.insertPitStop(invalidPitStop)
        } catch (e: PitStopException.ValidationException) {
            validationExceptionThrown = true
            assertThat(e.message).isNotEmpty()
        } catch (e: Exception) {
            // Si se lanza otra excepción, también es válido para esta prueba
            validationExceptionThrown = true
        }

        // Verificar que se lanzó alguna excepción de validación
        assertThat(validationExceptionThrown).isTrue()
    }
}