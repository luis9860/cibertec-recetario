package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        if (email.isBlank() || password.length < 8) {
            return AppResult.Error(pe.edu.cibertec.recetario.core.result.AppError.ValidationError("Datos inválidos"))
        }
        return repository.login(email, password)
    }
}
