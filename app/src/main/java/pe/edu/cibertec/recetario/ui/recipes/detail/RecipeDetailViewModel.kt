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

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val errorMessage: String? = null,
    val isDeleted: Boolean = false
)

class RecipeDetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getRecipeDetailUseCase(id)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, recipe = result.data) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
                }
            }
        }
    }

    fun deleteRecipe() {
        val id = _uiState.value.recipe?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = deleteRecipeUseCase(id)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isDeleted = true) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.error.toString()) }
            }
        }
    }
}
