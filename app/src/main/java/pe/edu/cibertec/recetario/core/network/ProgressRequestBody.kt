package pe.edu.cibertec.recetario.core.network

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

/**
 * Clase personalizada de RequestBody para OkHttp que permite rastrear el progreso 
 * de la subida de un archivo (como una imagen o un video).
 * Esto es lo que permite mostrar una barra de progreso real cuando subes una receta.
 */
class ProgressRequestBody(
    private val contentResolver: ContentResolver, // Para acceder a los archivos del dispositivo
    private val uri: Uri,                         // La ubicación del archivo (URI)
    private val contentType: String,              // El tipo de archivo (ej: "image/jpeg" o "video/mp4")
    private val onProgress: (percent: Int) -> Unit // Función de retorno (callback) para actualizar la barra de progreso
) : RequestBody() {

    // Retorna el tipo de contenido del archivo
    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    // Calcula el tamaño total del archivo en bytes
    override fun contentLength(): Long {
        return try {
            contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
        } catch (e: IOException) {
            -1L
        }
    }

    /**
     * Escribe los datos del archivo en el "sink" (la tubería de red) de OkHttp.
     * Mientras escribe, calcula cuántos bytes han pasado y llama a onProgress.
     */
    override fun writeTo(sink: BufferedSink) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val totalBytes = contentLength()
        var bytesWritten = 0L

        try {
            val buffer = ByteArray(8192) // Lee el archivo en trozos de 8KB
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read) // Envía el trozo por internet
                bytesWritten += read
                
                // Si conocemos el tamaño total, calculamos el porcentaje
                if (totalBytes > 0) {
                    val progress = ((bytesWritten.toDouble() / totalBytes) * 100).toInt()
                    onProgress(progress) // Avisamos a la UI para que mueva la barra
                }
            }
        } finally {
            inputStream.close() // Cerramos el archivo al terminar
        }
    }
}
