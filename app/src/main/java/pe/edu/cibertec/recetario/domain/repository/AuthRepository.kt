package pe.edu.cibertec.recetario.domain.repository

import pe.edu.cibertec.recetario.core.result.AppResult

/**
 * Interfaz del Repositorio de Autenticación.
 * Define las operaciones que la aplicación puede realizar relacionadas con la gestión de usuarios.
 * Al ser una interfaz, desacopla la lógica de negocio de la implementación técnica (PHP, Firebase, etc.).
 */
interface AuthRepository {
    // Inicia sesión con correo y contraseña
    suspend fun login(email: String, password: String): AppResult<Unit>
    
    // Registra un nuevo usuario
    suspend fun register(email: String, password: String): AppResult<Unit>
    
    // Cierra la sesión activa
    fun logout()
    
    // Verifica si la sesión del usuario actual sigue siendo válida
    fun isSessionValid(): Boolean
}
