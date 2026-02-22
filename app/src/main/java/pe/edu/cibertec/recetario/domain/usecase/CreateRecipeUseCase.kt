package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para la creación de una nueva receta.
 * Coordina el proceso de envío de los datos de la receta hacia el repositorio.
 */
class CreateRecipeUseCase(
    private val repository: RecipeRepository // Repositorio que maneja la persistencia de recetas
) {
    /**
     * Ejecuta el proceso de creación.
     * @param recipe Objeto de dominio que contiene la información de la receta a crear.
     * @return El ID de la nueva receta creada envuelto en un AppResult.
     */
    suspend operator fun invoke(recipe: Recipe): AppResult<Long> {
        return repository.createRecipe(recipe)
    }
}
