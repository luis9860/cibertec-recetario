package pe.edu.cibertec.recetario.data.repository

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MultipartBody
import pe.edu.cibertec.recetario.core.network.ProgressRequestBody
import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.repository.MediaRepository

class MediaRepositoryImpl(
    private val apiService: ApiService,
    private val contentResolver: ContentResolver
) : MediaRepository {
    override suspend fun uploadFile(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String> {
        return try {
            val requestBody = ProgressRequestBody(contentResolver, uri, mimeType, onProgress)
            val body = MultipartBody.Part.createFormData("file", "upload_file", requestBody)

            val response = apiService.uploadFile(body)
            if (response.isSuccessful && response.body()?.ok == true) {
                AppResult.Success(response.body()!!.url!!)
            } else {
                val errorMsg = response.body()?.error ?: "Error al subir archivo (${response.code()})"
                AppResult.Error(AppError.ServerError(errorMsg))
            }
        } catch (e: Exception) {
            AppResult.Error(AppError.ServerError(e.message ?: "Error desconocido"))
        }
    }
}
