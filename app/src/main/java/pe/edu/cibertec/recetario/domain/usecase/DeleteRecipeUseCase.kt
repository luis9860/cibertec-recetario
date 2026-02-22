package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

class DeleteRecipeUseCase(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(id: Long): AppResult<Unit> = recipeRepository.deleteRecipe(id)
}
