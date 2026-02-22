package pe.edu.cibertec.recetario.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de Sesión del Usuario.
 * Utiliza SharedPreferences para almacenar de forma persistente (aunque la app se cierre)
 * el token de acceso, el correo del usuario y la fecha de expiración de la sesión.
 */
class SessionManager(context: Context) {
    // Archivo de preferencias privado llamado "recetario_prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences("recetario_prefs", Context.MODE_PRIVATE)

    companion object {
        // Claves para identificar cada dato guardado
        const val KEY_TOKEN = "key_token"
        const val KEY_EXPIRES_AT = "key_expires_at"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        const val KEY_USER_EMAIL = "key_user_email"
    }

    /**
     * Guarda los datos recibidos del servidor tras un Login exitoso.
     */
    fun saveSession(token: String, email: String?, expiresAt: Long) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putLong(KEY_EXPIRES_AT, expiresAt)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply() // Guarda los cambios de forma asíncrona
        }
    }

    // Obtiene el Token JWT necesario para las peticiones a la API
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // Obtiene el email del usuario logueado
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    /**
     * Verifica si el token guardado todavía es válido comparando la fecha actual
     * con la fecha de expiración proporcionada por el servidor.
     */
    fun isTokenValid(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0)
        // Multiplicamos por 1000L porque el servidor suele enviar segundos y Android usa milisegundos
        return System.currentTimeMillis() < expiresAt * 1000L
    }

    /**
     * Comprueba si hay una sesión activa y si el token no ha caducado.
     */
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && isTokenValid()
    }

    /**
     * Elimina todos los datos guardados. Se usa al cerrar sesión (Logout).
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
