package pe.edu.cibertec.recetario.domain.repository

import pe.edu.cibertec.recetario.core.result.AppResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun register(email: String, password: String): AppResult<Unit>
    fun logout()
    fun isSessionValid(): Boolean
}
