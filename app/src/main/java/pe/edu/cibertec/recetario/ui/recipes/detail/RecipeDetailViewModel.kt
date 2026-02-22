package pe.edu.cibertec.recetario.ui.recipes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.usecase.DeleteRecipeUseCase
import pe.edu.cibertec.recetario.domain.usecase.GetRecipeDetailUseCase

/**
 * Estado de la interfaz de usuario para el Detalle de la Receta.
 */
data class RecipeDetailUiState(
    val isLoading: Boolean = false, // Indica si se están cargando los datos
    val recipe: Recipe? = null,     // Objeto receta que contiene toda la información a mostrar
    val errorMessage: String? = null, // Mensaje de error en caso de fallo
    val isDeleted: Boolean = false    // Indica si la receta ha sido eliminada con éxito
)

/**
 * ViewModel encargado de la lógica para mostrar y eliminar una receta.
 */
class RecipeDetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase, // Caso de uso para obtener datos de la receta
    private val deleteRecipeUseCase: DeleteRecipeUseCase       // Caso de uso para eliminar la receta
) : ViewModel() {

    // Estado interno mutable
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    // Estado expuesto para que el fragmento lo observe
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    /**
     * Carga la información de una receta desde el servidor.
     * @param id Identificador único de la receta.
     */
    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            // Iniciamos estado de carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Ejecutamos el caso de uso
            when (val result = getRecipeDetailUseCase(id)) {
                is AppResult.Success -> {
                    // Actualizamos con los datos recibidos
                    _uiState.update { it.copy(isLoading = false, recipe = result.data) }
                }
                is AppResult.Error -> {
                    // Informamos del error
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }

    /**
     * Lógica para eliminar la receta actual.
     */
    fun deleteRecipe() {
        val id = _uiState.value.recipe?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Ejecutamos el caso de uso de eliminación
            when (val result = deleteRecipeUseCase(id)) {
                is AppResult.Success -> {
                    // Indicamos que se eliminó correctamente
                    _uiState.update { it.copy(isLoading = false, isDeleted = true) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }
}
