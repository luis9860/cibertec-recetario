package pe.edu.cibertec.recetario.ui.recipes.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.usecase.CreateRecipeUseCase
import pe.edu.cibertec.recetario.domain.usecase.UploadFileUseCase

data class AddRecipeUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val uploadStatus: String? = null,
    val imageProgress: Int = 0,
    val videoProgress: Int = 0,
    val selectedImageUri: Uri? = null,
    val selectedVideoUri: Uri? = null,
    val videoInfo: String? = null
)

class AddRecipeViewModel(
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onVideoSelected(uri: Uri, info: String) {
        _uiState.update { it.copy(selectedVideoUri = uri, videoInfo = info) }
    }

    fun createRecipe(title: String, description: String, visibility: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, uploadStatus = null) }

            var finalImageUrl: String? = null
            var finalVideoUrl: String? = null

            // 1. Subir Imagen si existe
            _uiState.value.selectedImageUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo imagen...") }
                val result = uploadFileUseCase(uri, "image/*") { progress ->
                    _uiState.update { it.copy(imageProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalImageUrl = result.data
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir imagen: ${result.error}") }
                        return@launch
                    }
                }
            }

            // 2. Subir Video si existe
            _uiState.value.selectedVideoUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo video...") }
                val result = uploadFileUseCase(uri, "video/mp4") { progress ->
                    _uiState.update { it.copy(videoProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalVideoUrl = result.data
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir video: ${result.error}") }
                        return@launch
                    }
                }
            }

            // 3. Crear Receta
            _uiState.update { it.copy(uploadStatus = "Finalizando receta...") }
            val recipe = Recipe(
                id = 0,
                userId = 0,
                title = title,
                description = description,
                visibility = visibility,
                createdAt = System.currentTimeMillis() / 1000,
                imageUrl = finalImageUrl,
                videoUrl = finalVideoUrl,
                authorEmail = null,
                isMine = true
            )

            when (val result = createRecipeUseCase(recipe)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }
}
