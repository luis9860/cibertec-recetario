package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateRecipeRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("visibility") val visibility: String = "public",
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis() / 1000,
    @SerializedName("urlImagen") val urlImagen: String? = null,
    @SerializedName("urlVideo") val urlVideo: String? = null
)
