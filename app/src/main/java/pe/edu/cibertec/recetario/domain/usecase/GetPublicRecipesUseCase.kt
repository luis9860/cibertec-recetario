package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para obtener las recetas públicas.
 * Recupera la lista de todas las recetas compartidas por los usuarios desde el repositorio.
 */
class GetPublicRecipesUseCase(private val repository: RecipeRepository) {
    /**
     * Ejecuta la solicitud para obtener recetas públicas.
     * @return Una lista de recetas públicas envuelta en un AppResult.
     */
    suspend operator fun invoke(): AppResult<List<Recipe>> = repository.getPublicRecipes()
}
