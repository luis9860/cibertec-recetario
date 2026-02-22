package pe.edu.cibertec.recetario.ui.recipes.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.usecase.UpdateRecipeUseCase
import pe.edu.cibertec.recetario.domain.usecase.UploadFileUseCase

/**
 * Estado de la interfaz de usuario para la pantalla de Edición de Receta.
 */
data class EditRecipeUiState(
    val isLoading: Boolean = false,         // Indica si hay alguna operación en curso
    val isSuccess: Boolean = false,         // Indica si se guardaron los cambios correctamente
    val errorMessage: String? = null,        // Almacena errores para mostrar al usuario
    val uploadStatus: String? = null,        // Estado actual (Ej: "Subiendo imagen...")
    val imageProgress: Int = 0,             // Progreso de subida de imagen (0-100)
    val videoProgress: Int = 0,             // Progreso de subida de video (0-100)
    val selectedImageUri: Uri? = null,      // Nueva imagen seleccionada localmente
    val selectedVideoUri: Uri? = null       // Nuevo video seleccionado localmente
)

/**
 * ViewModel para gestionar la edición de una receta existente.
 * Permite actualizar campos de texto y subir nuevos archivos multimedia.
 */
class EditRecipeViewModel(
    private val updateRecipeUseCase: UpdateRecipeUseCase, // Caso de uso para actualizar datos
    private val uploadFileUseCase: UploadFileUseCase     // Caso de uso para subir archivos
) : ViewModel() {

    // Estado observable
    private val _uiState = MutableStateFlow(EditRecipeUiState())
    val uiState: StateFlow<EditRecipeUiState> = _uiState.asStateFlow()

    // Maneja la selección de una nueva imagen
    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    // Maneja la selección de un nuevo video
    fun onVideoSelected(uri: Uri) {
        _uiState.update { it.copy(selectedVideoUri = uri) }
    }

    /**
     * Proceso de actualización de la receta.
     * Si el usuario eligió nuevos archivos, los sube primero antes de actualizar los datos.
     */
    fun updateRecipe(
        id: Long,
        title: String,
        description: String,
        visibility: String,
        currentImageUrl: String?,
        currentVideoUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, uploadStatus = null) }

            // Por defecto mantenemos las URLs actuales
            var finalImageUrl = currentImageUrl
            var finalVideoUrl = currentVideoUrl

            // 1. Subir nueva imagen si se seleccionó una
            _uiState.value.selectedImageUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo imagen...") }
                val result = uploadFileUseCase(uri, "image/*") { progress ->
                    _uiState.update { it.copy(imageProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalImageUrl = result.data // Actualizamos URL
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir imagen") }
                        return@launch
                    }
                }
            }

            // 2. Subir nuevo video si se seleccionó uno
            _uiState.value.selectedVideoUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo video...") }
                val result = uploadFileUseCase(uri, "video/mp4") { progress ->
                    _uiState.update { it.copy(videoProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalVideoUrl = result.data // Actualizamos URL
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir video") }
                        return@launch
                    }
                }
            }

            // 3. Guardar todos los cambios finales en la base de datos MySQL
            _uiState.update { it.copy(uploadStatus = "Guardando cambios...") }
            when (val result = updateRecipeUseCase(id, title, description, visibility, finalImageUrl, finalVideoUrl)) {
                is AppResult.Success -> {
                    // Éxito: informamos que se guardó correctamente
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }
}
