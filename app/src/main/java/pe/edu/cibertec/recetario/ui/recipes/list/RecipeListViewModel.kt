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

/**
 * ViewModel para la pantalla de Listado de Recetas.
 * Maneja la lógica de obtener recetas públicas, privadas y realizar búsquedas.
 */
class RecipeListViewModel(
    private val getPublicRecipesUseCase: GetPublicRecipesUseCase, // Caso de uso para recetas públicas
    private val getMyRecipesUseCase: GetMyRecipesUseCase,         // Caso de uso para mis recetas
    private val searchRecipesUseCase: SearchRecipesUseCase        // Caso de uso para búsqueda
) : ViewModel() {

    // Estado de la UI que contiene la lista de recetas y el estado de carga
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    /**
     * Carga las recetas desde el servidor.
     * @param isPrivate Si es true, carga las recetas del usuario logueado. Si es false, las públicas.
     */
    fun loadRecipes(isPrivate: Boolean = false) {
        viewModelScope.launch {
            // Indicamos que ha comenzado la carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Decidimos qué caso de uso ejecutar según el parámetro
            val result = if (isPrivate) {
                getMyRecipesUseCase()
            } else {
                getPublicRecipesUseCase()
            }

            when (result) {
                is AppResult.Success -> {
                    // Filtramos las recetas según su visibilidad para mayor seguridad en la UI
                    val filteredRecipes = if (isPrivate) {
                        result.data.filter { it.visibility.lowercase() == "private" }
                    } else {
                        result.data.filter { it.visibility.lowercase() == "public" }
                    }

                    // Actualizamos la lista de recetas en el estado
                    _uiState.update { it.copy(
                        isLoading = false,
                        recipes = filteredRecipes
                    )}
                }
                is AppResult.Error -> {
                    // Manejamos el error actualizando el estado
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${result.error}"
                    )}
                }
            }
        }
    }

    /**
     * Realiza una búsqueda de recetas por texto.
     */
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
