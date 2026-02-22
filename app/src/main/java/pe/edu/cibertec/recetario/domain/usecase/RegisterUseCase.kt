package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

/**
 * Caso de Uso para el registro de un nuevo usuario.
 * Coordina el proceso de creación de cuenta mediante el repositorio de autenticación.
 */
class RegisterUseCase(private val repository: AuthRepository) {
    /**
     * Ejecuta el proceso de registro.
     * @param email Correo electrónico ingresado por el usuario.
     * @param password Contraseña elegida por el usuario.
     * @return El resultado de la operación (éxito o error).
     */
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        // Validación de reglas de negocio antes de proceder con el registro
        if (email.isBlank() || password.length < 8) {
            return AppResult.Error(AppError.ValidationError("Datos inválidos (password min 8 carac.)"))
        }
        return repository.register(email, password)
    }
}
