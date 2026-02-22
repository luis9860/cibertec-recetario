package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Objetos de Transferencia de Datos (DTO) para la Autenticación.
 * Estas clases representan la estructura exacta de los datos JSON que se envían y reciben 
 * desde los archivos PHP del servidor (auth_login.php, auth_register.php).
 */

// Estructura para enviar los datos de inicio de sesión al servidor
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Estructura para recibir la respuesta del servidor tras intentar loguearse
data class LoginResponse(
    @SerializedName("ok") val ok: Boolean, // Indica si la operación fue exitosa (true/false)
    @SerializedName("token") val token: String?, // Token JWT para futuras peticiones seguras
    @SerializedName("expiresAt") val expiresAt: Long?, // Fecha de expiración del token
    @SerializedName("message") val message: String? // Mensaje informativo o de error
)

// Estructura para enviar datos de nuevo usuario al servidor
data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Respuesta genérica para operaciones que solo necesitan confirmar éxito o fallo (ej: Logout, Delete)
data class GenericResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("message") val message: String?
)
