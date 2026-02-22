package pe.edu.cibertec.recetario.domain.repository

import android.net.Uri
import pe.edu.cibertec.recetario.core.result.AppResult

interface MediaRepository {
    suspend fun uploadFile(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String>
}
