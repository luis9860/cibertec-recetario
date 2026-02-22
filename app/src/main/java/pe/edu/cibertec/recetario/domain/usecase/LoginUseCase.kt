package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

/**
 * Caso de Uso para el inicio de sesión.
 * Coordina la autenticación del usuario mediante el repositorio de autenticación.
 */
class LoginUseCase(private val repository: AuthRepository) {
    /**
     * Ejecuta el proceso de inicio de sesión.
     * @param email Correo electrónico ingresado por el usuario.
     * @param password Contraseña ingresada por el usuario.
     * @return El resultado de la operación (éxito o error).
     */
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        // Validación básica de negocio antes de llamar al repositorio
        if (email.isBlank() || password.length < 8) {
            return AppResult.Error(pe.edu.cibertec.recetario.core.result.AppError.ValidationError("Datos inválidos"))
        }
        return repository.login(email, password)
    }
}
