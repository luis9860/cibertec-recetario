package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateRecipeRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("visibility") val visibility: String? = null,
    @SerializedName("urlImagen") val urlImagen: String? = null,
    @SerializedName("urlVideo") val urlVideo: String? = null
)
