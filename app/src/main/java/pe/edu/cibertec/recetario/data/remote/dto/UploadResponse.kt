package pe.edu.cibertec.recetario.data.remote.dto

data class UploadResponse(
    val ok: Boolean,
    val url: String?,
    val tipo: String?,
    val size: Long?,
    val error: String?
)
