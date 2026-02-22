package pe.edu.cibertec.recetario.ui.recipes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.usecase.GetMyRecipesUseCase
import pe.edu.cibertec.recetario.domain.usecase.GetPublicRecipesUseCase
import pe.edu.cibertec.recetario.domain.usecase.SearchRecipesUseCase

class RecipeListViewModel(
    private val getPublicRecipesUseCase: GetPublicRecipesUseCase,
    private val getMyRecipesUseCase: GetMyRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    fun loadRecipes(isPrivate: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (isPrivate) {
                getMyRecipesUseCase()
            } else {
                getPublicRecipesUseCase()
            }

            when (result) {
                is AppResult.Success -> {
                    val filteredRecipes = if (isPrivate) {
                        result.data.filter { it.visibility.lowercase() == "private" }
                    } else {
                        result.data.filter { it.visibility.lowercase() == "public" }
                    }

                    _uiState.update { it.copy(
                        isLoading = false,
                        recipes = filteredRecipes
                    )}
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${result.error}"
                    )}
                }
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = searchRecipesUseCase(query)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipes = result.data
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: ${result.error}"
                        )
                    }
                }
            }
        }
    }
}
