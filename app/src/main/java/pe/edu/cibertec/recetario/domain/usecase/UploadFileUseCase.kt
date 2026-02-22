package pe.edu.cibertec.recetario.domain.usecase

import android.net.Uri
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.MediaRepository

class UploadFileUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String> = repository.uploadFile(uri, mimeType, onProgress)
}
