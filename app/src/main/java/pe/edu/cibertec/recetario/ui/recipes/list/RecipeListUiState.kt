package pe.edu.cibertec.recetario.ui.recipes.list

import pe.edu.cibertec.recetario.domain.model.Recipe

/**
 * Estado de la Interfaz de Usuario para la Lista de Recetas.
 * Define qué información necesita la pantalla en cada momento.
 */
data class RecipeListUiState(
    val isLoading: Boolean = false,    // Indica si se están descargando las recetas del servidor
    val recipes: List<Recipe> = emptyList(), // Contiene la lista de recetas que se mostrarán en el RecyclerView
    val errorMessage: String? = null    // Almacena un mensaje de error si la conexión falla
)
