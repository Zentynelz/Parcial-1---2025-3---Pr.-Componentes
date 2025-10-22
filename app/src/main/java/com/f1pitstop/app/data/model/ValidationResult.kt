package com.f1pitstop.app.data.model

/**
 * Resultado de una validación de datos
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
) {
    /**
     * Obtiene todos los errores como un string concatenado
     * @return String con todos los errores separados por salto de línea
     */
    fun getAllErrorsAsString(): String {
        return errors.joinToString("\n")
    }
    
    companion object {
        /**
         * Crea un resultado válido
         * @return ValidationResult válido sin errores
         */
        fun valid(): ValidationResult {
            return ValidationResult(true, emptyList())
        }
        
        /**
         * Crea un resultado inválido con errores
         * @param errors Lista de errores
         * @return ValidationResult inválido con errores
         */
        fun invalid(errors: List<String>): ValidationResult {
            return ValidationResult(false, errors)
        }
        
        /**
         * Crea un resultado inválido con un solo error
         * @param error Error único
         * @return ValidationResult inválido con un error
         */
        fun invalid(error: String): ValidationResult {
            return ValidationResult(false, listOf(error))
        }
    }
}