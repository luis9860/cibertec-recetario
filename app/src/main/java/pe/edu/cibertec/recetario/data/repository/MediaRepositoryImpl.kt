package pe.edu.cibertec.recetario.data.repository

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MultipartBody
import pe.edu.cibertec.recetario.core.network.ProgressRequestBody
import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.repository.MediaRepository

/**
 * Implementación del Repositorio de Medios.
 * Gestiona la subida de archivos (imágenes y videos) al servidor utilizando peticiones Multipart.
 */
class MediaRepositoryImpl(
    private val apiService: ApiService, // Servicio Retrofit para la API
    private val contentResolver: ContentResolver // Necesario para leer archivos del dispositivo mediante URIs
) : MediaRepository {

    /**
     * Sube un archivo al servidor y reporta el progreso de la subida.
     * @param uri URI del archivo local seleccionado por el usuario.
     * @param mimeType Tipo de archivo (ej: "image/jpeg", "video/mp4").
     * @param onProgress Callback que recibe el porcentaje de subida (0-100).
     */
    override suspend fun uploadFile(
        uri: Uri,
        mimeType: String,
        onProgress: (Int) -> Unit
    ): AppResult<String> {
        return try {
            // Utilizamos ProgressRequestBody para poder rastrear cuánto se va subiendo en tiempo real
            val requestBody = ProgressRequestBody(contentResolver, uri, mimeType, onProgress)
            
            // Creamos la parte "Multipart" que espera el servidor PHP en la variable $_FILES['file']
            val body = MultipartBody.Part.createFormData("file", "upload_file", requestBody)

            // Realizamos la petición de subida
            val response = apiService.uploadFile(body)
            
            if (response.isSuccessful && response.body()?.ok == true) {
                // Si tuvo éxito, devolvemos la URL pública del archivo en el servidor
                AppResult.Success(response.body()!!.url!!)
            } else {
                // Si el servidor reporta un error (ej: archivo muy grande), lo capturamos
                val errorMsg = response.body()?.error ?: "Error al subir archivo (${response.code()})"
                AppResult.Error(AppError.ServerError(errorMsg))
            }
        } catch (e: Exception) {
            // Error de conexión o de lectura de archivo
            AppResult.Error(AppError.ServerError(e.message ?: "Error desconocido"))
        }
    }
}
