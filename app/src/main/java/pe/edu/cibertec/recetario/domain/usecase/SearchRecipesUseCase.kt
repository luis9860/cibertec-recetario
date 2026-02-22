package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para buscar recetas.
 * Permite filtrar el listado de recetas basándose en un criterio de búsqueda (título o descripción).
 */
class SearchRecipesUseCase(private val recipeRepository: RecipeRepository) {
    /**
     * Ejecuta la búsqueda de recetas.
     * @param query El texto a buscar.
     * @return El resultado de la búsqueda envuelto en un AppResult.
     */
    suspend operator fun invoke(query: String) = recipeRepository.searchRecipes(query)
}
