package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

class GetRecipeDetailUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: Long): AppResult<Recipe> = repository.getRecipeDetail(id)
}
