package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

class SearchRecipesUseCase(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(query: String) = recipeRepository.searchRecipes(query)
}
