package pe.edu.cibertec.recetario.domain.repository

import android.net.Uri
import pe.edu.cibertec.recetario.core.result.AppResult

/**
 * Interfaz del Repositorio de Medios.
 * Define la acción de subir archivos (imagen/video) al servidor.
 * La implementación se encarga de transformar el archivo local en una petición de red.
 */
interface MediaRepository {
    /**
     * Sube un archivo al servidor.
     * @param uri Ubicación local del archivo.
     * @param mimeType Tipo de archivo (image/jpeg, video/mp4, etc.).
     * @param onProgress Función callback para reportar el porcentaje de subida a la UI.
     * @return El resultado de la operación conteniendo la URL final del archivo subido.
     */
    suspend fun uploadFile(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String>
}
