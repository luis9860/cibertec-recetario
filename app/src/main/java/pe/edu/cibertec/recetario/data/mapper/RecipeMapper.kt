package pe.edu.cibertec.recetario.data.mapper

import pe.edu.cibertec.recetario.core.Config
import pe.edu.cibertec.recetario.data.remote.dto.CreateRecipeRequest
import pe.edu.cibertec.recetario.data.remote.dto.RecipeDto
import pe.edu.cibertec.recetario.domain.model.Recipe

class RecipeMapper {

    fun toDomain(dto: RecipeDto): Recipe {
        return Recipe(
            id = dto.id,
            userId = dto.userId ?: 0L,
            title = dto.title,
            description = dto.description,
            visibility = dto.visibility,
            createdAt = dto.createdAt,
            imageUrl = formatUrl(dto.imageUrl),
            videoUrl = formatUrl(dto.videoUrl),
            authorEmail = dto.authorEmail,
            isMine = dto.isMine ?: false
        )
    }

    private fun formatUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        
        // Si el servidor devuelve "localhost", lo reemplazamos por la IP configurada
        if (path.startsWith("http")) {
            return path.replace("localhost", Config.ACTUAL_IP)
        }
        
        // Si no es una URL completa, concatenamos con la BASE_URL configurada
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return Config.BASE_URL + cleanPath
    }

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
