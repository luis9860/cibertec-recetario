package pe.edu.cibertec.recetario.data.repository

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.local.SessionManager
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<Unit> {
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                val body = response.body()
                val ok = body?.get("ok") as? Boolean ?: false
                if (ok) {
                    val token = body?.get("token") as? String ?: ""
                    val expiresAt = (body?.get("expiresAt") as? Number)?.toLong() ?: 0L
                    
                    // CORRECCIÓN: Usar el nuevo método saveSession
                    sessionManager.saveSession(token, email, expiresAt)
                    
                    AppResult.Success(Unit)
                } else {
                    AppResult.Error(AppError.ServerError(body?.get("message") as? String ?: "Error en la respuesta"))
                }
            } else {
                AppResult.Error(AppError.Unauthorized)
            }
        } catch (e: Exception) {
            AppResult.Error(AppError.NetworkError)
        }
    }

    override suspend fun register(email: String, password: String): AppResult<Unit> {
        return try {
            val response = apiService.register(mapOf("email" to email, "password" to password))
            if (response.isSuccessful && response.body()?.get("ok") == true) {
                AppResult.Success(Unit)
            } else {
                AppResult.Error(AppError.ServerError("Error en el registro"))
            }
        } catch (e: Exception) {
            AppResult.Error(AppError.NetworkError)
        }
    }

    override fun logout() {
        sessionManager.clearSession()
    }

    override fun isSessionValid(): Boolean {
        return sessionManager.isTokenValid()
    }
}
