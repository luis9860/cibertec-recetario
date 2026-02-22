package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("visibility") val visibility: String,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("image") val imageUrl: String?,
    @SerializedName("video") val videoUrl: String?,
    @SerializedName("authorEmail") val authorEmail: String?,
    @SerializedName("isMine") val isMine: Boolean? = false
)

data class RecipeListResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("items") val items: List<RecipeDto>
)

data class RecipeDetailResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("item") val item: RecipeDto
)

data class RecipeResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("id") val id: Long? = null,
    @SerializedName("message") val message: String? = null
)
