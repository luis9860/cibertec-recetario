package pe.edu.cibertec.recetario.core.result

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
}

sealed class AppError {
    object NetworkError : AppError()
    object Unauthorized : AppError()
    data class ServerError(val message: String) : AppError()
    data class ValidationError(val message: String) : AppError()
    object UnknownError : AppError()
}
