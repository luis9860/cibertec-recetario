package pe.edu.cibertec.recetario.data.repository

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.local.SessionManager
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.repository.AuthRepository

/**
 * Implementación del Repositorio de Autenticación.
 * Gestiona el proceso de Login, Registro y el estado de la sesión del usuario.
 */
class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    /**
     * Realiza el inicio de sesión enviando las credenciales al servidor.
     * Si es exitoso, guarda el Token JWT y los datos del usuario localmente.
     */
    override suspend fun login(email: String, password: String): AppResult<Unit> {
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                val body = response.body()
                val ok = body?.get("ok") as? Boolean ?: false
                if (ok) {
                    // Extrae los datos de la respuesta JSON
                    val token = body?.get("token") as? String ?: ""
                    val expiresAt = (body?.get("expiresAt") as? Number)?.toLong() ?: 0L
                    
                    // Guarda la sesión de forma persistente
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

    /**
     * Envía la solicitud de registro de un nuevo usuario al servidor PHP.
     */
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

    /**
     * Cierra la sesión del usuario eliminando los datos locales.
     */
    override fun logout() {
        sessionManager.clearSession()
    }

    /**
     * Verifica si la sesión actual sigue siendo válida.
     */
    override fun isSessionValid(): Boolean {
        return sessionManager.isTokenValid()
    }
}
