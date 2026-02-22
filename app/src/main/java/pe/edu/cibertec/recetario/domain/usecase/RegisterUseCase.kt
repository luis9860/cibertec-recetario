package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        if (email.isBlank() || password.length < 8) {
            return AppResult.Error(AppError.ValidationError("Datos inválidos (password min 8 carac.)"))
        }
        return repository.register(email, password)
    }
}
