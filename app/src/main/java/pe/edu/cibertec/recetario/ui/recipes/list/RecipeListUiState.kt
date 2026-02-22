package pe.edu.cibertec.recetario.ui.recipes.list

import pe.edu.cibertec.recetario.domain.model.Recipe

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val errorMessage: String? = null
)
