package pe.edu.cibertec.recetario.data.local;

import android.content.Context;
import android.content.SharedPreferences;

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("recetario_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "key_token"
        const val KEY_EXPIRES_AT = "key_expires_at"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        const val KEY_USER_EMAIL = "key_user_email"
    }

    fun saveSession(token: String, email: String?, expiresAt: Long) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putLong(KEY_EXPIRES_AT, expiresAt)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun isTokenValid(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0)
        return System.currentTimeMillis() < expiresAt * 1000L
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && isTokenValid()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
