package pe.edu.cibertec.recetario.core.result

/**
 * Clase sellada (Sealed Class) para representar el resultado de una operación.
 * Se usa para envolver la respuesta de los repositorios y ViewModel,
 * permitiendo manejar estados de Éxito o Error de forma segura.
 */
sealed class AppResult<out T> {
    // Representa una operación exitosa que devuelve datos de tipo T
    data class Success<out T>(val data: T) : AppResult<T>()
    
    // Representa un fallo en la operación, conteniendo un objeto AppError detallado
    data class Error(val error: AppError) : AppResult<Nothing>()
}

/**
 * Tipos de errores comunes que pueden ocurrir en la aplicación.
 * Permite que la UI muestre mensajes específicos según el problema detectado.
 */
sealed class AppError {
    object NetworkError : AppError()        // Problemas de conexión a internet
    object Unauthorized : AppError()        // El token expiró o no tiene permiso
    data class ServerError(val message: String) : AppError() // El servidor respondió con un error (ej: 500)
    data class ValidationError(val message: String) : AppError() // Error en los datos enviados (ej: campos vacíos)
    object UnknownError : AppError()        // Cualquier otro error inesperado
}
