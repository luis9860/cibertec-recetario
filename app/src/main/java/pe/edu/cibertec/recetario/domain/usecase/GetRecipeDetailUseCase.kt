package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para obtener los detalles de una receta.
 * Recupera la información completa de una receta específica a partir de su ID.
 */
class GetRecipeDetailUseCase(private val repository: RecipeRepository) {
    /**
     * Ejecuta la solicitud para obtener los detalles de una receta.
     * @param id El identificador único de la receta.
     * @return El objeto de dominio Recipe envuelto en un AppResult.
     */
    suspend operator fun invoke(id: Long): AppResult<Recipe> = repository.getRecipeDetail(id)
}
