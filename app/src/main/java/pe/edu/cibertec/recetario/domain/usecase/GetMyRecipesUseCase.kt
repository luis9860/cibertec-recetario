package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para obtener las recetas creadas por el usuario actual.
 * Recupera la lista de recetas personales desde el repositorio.
 */
class GetMyRecipesUseCase(
    private val repository: RecipeRepository // Repositorio de recetas
) {
    /**
     * Ejecuta la solicitud para obtener las recetas propias.
     * @return Una lista de recetas del usuario envuelta en un AppResult.
     */
    suspend operator fun invoke(): AppResult<List<Recipe>> {
        return repository.getMyRecipes()
    }
}
