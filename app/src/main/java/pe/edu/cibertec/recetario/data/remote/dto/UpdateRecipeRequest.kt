package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Objeto de transferencia de datos para la actualización de una receta.
 * Envía los campos modificados al servidor PHP (recipes_update.php) 
 * utilizando el ID de la receta para identificar cuál debe ser editada.
 */
data class UpdateRecipeRequest(
    @SerializedName("id") val id: Long,               // Identificador único de la receta a editar
    @SerializedName("title") val title: String? = null, // Nuevo título (opcional)
    @SerializedName("description") val description: String? = null, // Nueva descripción (opcional)
    @SerializedName("visibility") val visibility: String? = null,   // Nueva visibilidad (opcional)
    @SerializedName("urlImagen") val urlImagen: String? = null,     // Nueva URL de imagen (si se cambió)
    @SerializedName("urlVideo") val urlVideo: String? = null        // Nueva URL de video (si se cambió)
)
