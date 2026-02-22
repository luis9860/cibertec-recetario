package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

class GetMyRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): AppResult<List<Recipe>> {
        return repository.getMyRecipes()
    }
}
