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

/**
 * Estado de la interfaz de usuario para la pantalla de Añadir Receta.
 * Controla el progreso de carga de archivos y los mensajes informativos.
 */
data class AddRecipeUiState(
    val isLoading: Boolean = false,         // Indica si hay alguna operación en curso
    val isSuccess: Boolean = false,         // Éxito al guardar la receta
    val errorMessage: String? = null,        // Mensaje de error para el usuario
    val uploadStatus: String? = null,        // Texto descriptivo del progreso actual (Ej: "Subiendo imagen...")
    val imageProgress: Int = 0,             // Porcentaje de subida de la imagen (0-100)
    val videoProgress: Int = 0,             // Porcentaje de subida del video (0-100)
    val selectedImageUri: Uri? = null,      // URI local de la imagen seleccionada
    val selectedVideoUri: Uri? = null,      // URI local del video seleccionado
    val videoInfo: String? = null           // Nombre y tamaño del video seleccionado
)

/**
 * ViewModel para la creación de nuevas recetas.
 * Coordina la subida de archivos multimedia y el registro final de la receta en el servidor.
 */
class AddRecipeViewModel(
    private val createRecipeUseCase: CreateRecipeUseCase, // Caso de uso para guardar la receta
    private val uploadFileUseCase: UploadFileUseCase     // Caso de uso para subir archivos al servidor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    // Actualiza el estado cuando el usuario elige una imagen
    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    // Actualiza el estado cuando el usuario elige un video
    fun onVideoSelected(uri: Uri, info: String) {
        _uiState.update { it.copy(selectedVideoUri = uri, videoInfo = info) }
    }

    /**
     * Proceso principal de creación de receta.
     * Sigue el flujo: Subir Imagen -> Subir Video -> Guardar Receta.
     */
    fun createRecipe(title: String, description: String, visibility: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, uploadStatus = null) }

            var finalImageUrl: String? = null
            var finalVideoUrl: String? = null

            // 1. Subir Imagen si existe una seleccionada
            _uiState.value.selectedImageUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo imagen...") }
                val result = uploadFileUseCase(uri, "image/*") { progress ->
                    // Actualizamos la barra de progreso de la imagen
                    _uiState.update { it.copy(imageProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalImageUrl = result.data // Guardamos la URL recibida
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir imagen: ${result.error}") }
                        return@launch // Detenemos el proceso si falla la subida
                    }
                }
            }

            // 2. Subir Video si existe uno seleccionado
            _uiState.value.selectedVideoUri?.let { uri ->
                _uiState.update { it.copy(uploadStatus = "Subiendo video...") }
                val result = uploadFileUseCase(uri, "video/mp4") { progress ->
                    // Actualizamos la barra de progreso del video
                    _uiState.update { it.copy(videoProgress = progress) }
                }
                when (result) {
                    is AppResult.Success -> finalVideoUrl = result.data // Guardamos la URL recibida
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir video: ${result.error}") }
                        return@launch // Detenemos el proceso si falla la subida
                    }
                }
            }

            // 3. Crear Registro de la Receta con las URLs obtenidas
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

            // Enviamos los datos finales al servidor PHP
            when (val result = createRecipeUseCase(recipe)) {
                is AppResult.Success -> {
                    // Todo correcto: receta creada
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }
}
