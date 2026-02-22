package pe.edu.cibertec.recetario.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import pe.edu.cibertec.recetario.data.local.SessionManager

/**
 * Interceptor de Autenticación para OkHttp.
 * Actúa como un "peaje" por donde pasan todas las peticiones de red salientes.
 * Su función es añadir automáticamente el Token JWT a las cabeceras de cada solicitud.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    
    /**
     * Intercepta la petición antes de que salga a internet.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        
        // Si hay un token guardado en la sesión, lo añadimos a la cabecera "Authorization"
        sessionManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        // Continúa con la ejecución de la petición (ya con el token incluido)
        return chain.proceed(requestBuilder.build())
    }
}
