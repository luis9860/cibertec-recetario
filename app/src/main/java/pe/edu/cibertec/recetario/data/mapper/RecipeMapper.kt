package pe.edu.cibertec.recetario.data.mapper

import pe.edu.cibertec.recetario.core.Config
import pe.edu.cibertec.recetario.data.remote.dto.CreateRecipeRequest
import pe.edu.cibertec.recetario.data.remote.dto.RecipeDto
import pe.edu.cibertec.recetario.domain.model.Recipe

/**
 * Clase Mapper para la entidad Receta.
 * Su función es transformar los datos entre diferentes capas de la aplicación:
 * - DTO (Data Transfer Object): El formato crudo que viene del servidor PHP.
 * - Domain Model: El objeto limpio que usa la interfaz de usuario (UI).
 */
class RecipeMapper {

    /**
     * Convierte un objeto DTO (del servidor) a un objeto de Dominio (para la app).
     */
    fun toDomain(dto: RecipeDto): Recipe {
        return Recipe(
            id = dto.id,
            userId = dto.userId ?: 0L,
            title = dto.title,
            description = dto.description,
            visibility = dto.visibility,
            createdAt = dto.createdAt,
            // Formateamos las URLs para que apunten a la IP correcta del servidor
            imageUrl = formatUrl(dto.imageUrl),
            videoUrl = formatUrl(dto.videoUrl),
            authorEmail = dto.authorEmail,
            isMine = dto.isMine ?: false
        )
    }

    /**
     * Ajusta las URLs de las imágenes y videos.
     * Si el servidor devuelve "localhost" o una ruta relativa, la convierte en una URL completa 
     * accesible desde el dispositivo móvil.
     */
    private fun formatUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        
        // Corrección de IP para pruebas en red local o dominios específicos
        if (path.startsWith("http")) {
            return path.replace("localhost", Config.ACTUAL_IP)
        }
        
        // Construye la URL completa concatenando la BASE_URL configurada en Config.kt
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return Config.BASE_URL + cleanPath
    }

    /**
     * Convierte un objeto de Dominio de vuelta a DTO (si fuera necesario para el servidor).
     */
    fun toDto(domain: Recipe): RecipeDto {
        return RecipeDto(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            description = domain.description,
            visibility = domain.visibility,
            createdAt = domain.createdAt,
            imageUrl = domain.imageUrl,
            videoUrl = domain.videoUrl,
            authorEmail = domain.authorEmail,
            isMine = domain.isMine
        )
    }

    /**
     * Crea el objeto de petición necesario para enviar una nueva receta al servidor PHP.
     */
    fun toCreateRequest(domain: Recipe): CreateRecipeRequest {
        return CreateRecipeRequest(
            title = domain.title,
            description = domain.description,
            visibility = domain.visibility,
            createdAt = domain.createdAt,
            urlImagen = domain.imageUrl,
            urlVideo = domain.videoUrl
        )
    }
}
