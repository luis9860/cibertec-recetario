package pe.edu.cibertec.recetario.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.usecase.RegisterUseCase

/**
 * Estado de la interfaz de usuario para la pantalla de Registro.
 * Define las propiedades que el fragmento observará para actualizar su UI.
 */
data class RegisterUiState(
    val isLoading: Boolean = false,    // Indica si se está realizando la petición al servidor
    val isSuccess: Boolean = false,    // Indica si el registro fue exitoso
    val errorMessage: String? = null    // Almacena un mensaje de error si ocurre algún fallo
)

/**
 * ViewModel para la pantalla de Registro.
 * Gestiona el proceso de creación de una nueva cuenta de usuario.
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase // Caso de uso que encapsula la lógica de registro
) : ViewModel() {

    // Estado interno (mutable) que solo el ViewModel puede modificar
    private val _uiState = MutableStateFlow(RegisterUiState())
    // Estado expuesto (inmutable) que los fragmentos pueden observar
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Inicia el proceso de registro de un nuevo usuario.
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña elegida por el usuario.
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            // Actualiza el estado para indicar que ha comenzado la carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Ejecuta el caso de uso de registro
            when (val result = registerUseCase(email, password)) {
                is AppResult.Success -> {
                    // Si el registro es exitoso, actualiza el estado
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    // Mapea el tipo de error a un mensaje amigable para el usuario
                    val message = when (val error = result.error) {
                        is AppError.ServerError -> error.message
                        is AppError.ValidationError -> error.message
                        is AppError.NetworkError -> "Error de red. Intenta de nuevo."
                        is AppError.Unauthorized -> "No autorizado."
                        is AppError.UnknownError -> "Ocurrió un error desconocido."
                    }
                    // Actualiza el estado con el mensaje de error
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
            }
        }
    }
}
