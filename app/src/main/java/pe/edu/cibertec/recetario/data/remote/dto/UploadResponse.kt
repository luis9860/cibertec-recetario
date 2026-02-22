package pe.edu.cibertec.recetario.data.remote.dto

/**
 * Respuesta del servidor tras la subida de un archivo (Imagen o Video).
 * Contiene la URL final del archivo en el servidor y detalles sobre el mismo.
 */
data class UploadResponse(
    val ok: Boolean,      // Éxito o fallo de la subida
    val url: String?,     // URL pública para acceder al archivo (ej: http://.../uploads/img.jpg)
    val tipo: String?,    // Tipo de archivo detectado por el servidor (MIME type)
    val size: Long?,      // Tamaño final del archivo subido en bytes
    val error: String?    // Mensaje de error detallado en caso de fallo
)
