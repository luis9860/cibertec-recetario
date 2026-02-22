package pe.edu.cibertec.recetario.domain.usecase

import android.net.Uri
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.MediaRepository

/**
 * Caso de Uso para la subida de archivos multimedia.
 * Coordina el proceso de envío de imágenes y videos hacia el servidor, 
 * permitiendo monitorear el progreso de la subida.
 */
class UploadFileUseCase(private val repository: MediaRepository) {
    /**
     * Ejecuta el proceso de subida del archivo.
     * @param uri Dirección local del archivo en el dispositivo.
     * @param mimeType Tipo de archivo (image/jpeg, video/mp4, etc.).
     * @param onProgress Función de retorno para actualizar la barra de progreso en la UI.
     * @return La URL final del archivo en el servidor envuelta en un AppResult.
     */
    suspend operator fun invoke(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String> = repository.uploadFile(uri, mimeType, onProgress)
}
