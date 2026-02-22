package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para eliminar una receta.
 * Se encarga de solicitar al repositorio la eliminación de una receta específica mediante su ID.
 */
class DeleteRecipeUseCase(private val recipeRepository: RecipeRepository) {
    /**
     * Ejecuta la lógica de eliminación.
     * @param id El identificador único de la receta a eliminar.
     * @return Un objeto AppResult que indica si la operación fue exitosa o si hubo un error.
     */
    suspend operator fun invoke(id: Long): AppResult<Unit> = recipeRepository.deleteRecipe(id)
}
