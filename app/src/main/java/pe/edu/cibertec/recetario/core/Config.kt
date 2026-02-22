package pe.edu.cibertec.recetario.core

/**
 * Configuración global del proyecto.
 * Centraliza las constantes que pueden cambiar, como la dirección del servidor (IP).
 */
object Config {
    // CAMBIA AQUÍ LA IP PARA TODO EL PROYECTO
    // greenenergyperu.com es el dominio o IP donde está alojado el backend PHP
    private const val IP = "greenenergyperu.com"

    // URL base para las peticiones de Retrofit
    const val BASE_URL = "http://$IP/recetaapi/"
    
    // IP actual expuesta para otros usos (como Glide o depuración)
    const val ACTUAL_IP = IP
}
