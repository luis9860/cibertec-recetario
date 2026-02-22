package pe.edu.cibertec.recetario.domain.model

data class Recipe(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val visibility: String,
    val createdAt: Long,
    val imageUrl: String?,
    val videoUrl: String?,
    val authorEmail: String?,
    val isMine: Boolean
)
