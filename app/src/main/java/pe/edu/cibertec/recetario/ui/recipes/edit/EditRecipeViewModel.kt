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

data class EditRecipeUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val uploadStatus: String? = null,
    val imageProgress: Int = 0,
    val videoProgress: Int = 0,
    val selectedImageUri: Uri? = null,
    val selectedVideoUri: Uri? = null
)

class EditRecipeViewModel(
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditRecipeUiState())
    val uiState: StateFlow<EditRecipeUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onVideoSelected(uri: Uri) {
        _uiState.update { it.copy(selectedVideoUri = uri) }
    }

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

            var finalImageUrl = currentImageUrl
            var finalVideoUrl = currentVideoUrl

            // Subir nueva imagen si se seleccionó
            _uiState.value.selectedImageUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo imagen...") }
                val result = uploadFileUseCase(uri, "image/*") { progress ->
                    _uiState.update { it.copy(imageProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalImageUrl = result.data
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir imagen") }
                        return@launch
                    }
                }
            }

            // Subir nuevo video si se seleccionó
            _uiState.value.selectedVideoUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo video...") }
                val result = uploadFileUseCase(uri, "video/mp4") { progress ->
                    _uiState.update { it.copy(videoProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalVideoUrl = result.data
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir video") }
                        return@launch
                    }
                }
            }

            _uiState.update { it.copy(uploadStatus = "Guardando cambios...") }
            when (val result = updateRecipeUseCase(id, title, description, visibility, finalImageUrl, finalVideoUrl)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
            }
        }
    }
}
