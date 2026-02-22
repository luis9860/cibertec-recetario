package pe.edu.cibertec.recetario.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.usecase.LoginUseCase

/**
 * Estado de la interfaz de usuario para la pantalla de Login.
 * Representa si está cargando, si tuvo éxito o si hay un error.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel para la pantalla de Login.
 * Gestiona la lógica de autenticación separándola de la vista (Fragment).
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase // Caso de uso que contiene la lógica de negocio para loguearse
) : ViewModel() {

    // Estado interno mutable
    private val _uiState = MutableStateFlow(LoginUiState())
    // Estado público observable por el Fragment
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Ejecuta el proceso de inicio de sesión.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Indicamos que ha comenzado la carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Llamamos al caso de uso para verificar las credenciales
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> {
                    // Si es exitoso, actualizamos el estado para navegar al Home
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    // Si falla, mostramos un mensaje de error
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error en login: Verifique sus credenciales") }
                }
            }
        }
    }
}
