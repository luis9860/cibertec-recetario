package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Objeto de transferencia de datos para la creación de una receta.
 * Define los campos requeridos por el backend PHP (recipes_create.php) para guardar una nueva entrada.
 */
data class CreateRecipeRequest(
    @SerializedName("title") val title: String,            // Título de la receta
    @SerializedName("description") val description: String, // Procedimiento o descripción
    @SerializedName("visibility") val visibility: String = "public", // "public" o "private"
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis() / 1000, // Marca de tiempo en segundos
    @SerializedName("urlImagen") val urlImagen: String? = null, // Enlace a la imagen subida
    @SerializedName("urlVideo") val urlVideo: String? = null    // Enlace al video subido
)
