package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representación de una Receta en formato DTO (Data Transfer Object).
 * Mapea las columnas de la base de datos MySQL (vía PHP) a propiedades de Kotlin.
 */
data class RecipeDto(
    @SerializedName("id") val id: Long,               // ID único de la receta
    @SerializedName("user_id") val userId: Long?,      // ID del usuario creador
    @SerializedName("title") val title: String,        // Nombre del plato
    @SerializedName("description") val description: String, // Procedimiento
    @SerializedName("visibility") val visibility: String,   // Público o Privado
    @SerializedName("created_at") val createdAt: Long,     // Fecha de creación
    @SerializedName("image") val imageUrl: String?,         // Ruta de la imagen en el servidor
    @SerializedName("video") val videoUrl: String?,         // Ruta del video en el servidor
    @SerializedName("authorEmail") val authorEmail: String?, // Email del autor (para mostrar quién la subió)
    @SerializedName("isMine") val isMine: Boolean? = false  // Indica si la receta pertenece al usuario actual
)

/**
 * Respuesta del servidor para listados de recetas.
 */
data class RecipeListResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("items") val items: List<RecipeDto>
)

/**
 * Respuesta del servidor para el detalle de una sola receta.
 */
data class RecipeDetailResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("item") val item: RecipeDto
)

/**
 * Respuesta tras crear o actualizar una receta.
 */
data class RecipeResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("id") val id: Long? = null,
    @SerializedName("message") val message: String? = null
)
