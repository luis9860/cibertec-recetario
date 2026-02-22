package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

class UpdateRecipeUseCase(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        description: String,
        visibility: String,
        urlImagen: String?,
        urlVideo: String?
    ): AppResult<Unit> = recipeRepository.updateRecipe(id, title, description, visibility, urlImagen, urlVideo)
}
