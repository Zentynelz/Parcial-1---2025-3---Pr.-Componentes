// app/src/main/java/com/f1pitstop/app/ui/edit/EditPitStopViewModel.kt
package com.f1pitstop.app.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.f1pitstop.app.PitStopApplication
import com.f1pitstop.app.data.exception.PitStopException
import com.f1pitstop.app.data.model.PitStop

class EditPitStopViewModel(app: Application): AndroidViewModel(app) {
    private val repo = (app as PitStopApplication).repository
    
    suspend fun load(id: Long): PitStop? {
        return try {
            repo.getPitStop(id)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun save(p: PitStop): Long {
        return try {
            repo.upsertPitStop(p)
        } catch (e: PitStopException.ValidationException) {
            throw Exception("Error de validación: ${e.message}")
        } catch (e: PitStopException.DatabaseException) {
            throw Exception("Error de base de datos: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Error inesperado: ${e.message}")
        }
    }
}
