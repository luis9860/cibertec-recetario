package pe.edu.cibertec.recetario.domain.repository

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe

interface RecipeRepository {
    suspend fun getPublicRecipes(): AppResult<List<Recipe>>
    suspend fun getMyRecipes(): AppResult<List<Recipe>>
    suspend fun createRecipe(recipe: Recipe): AppResult<Long>
    suspend fun getRecipeDetail(id: Long): AppResult<Recipe>
    suspend fun updateRecipe(id: Long, title: String, description: String, visibility: String, urlImagen: String?, urlVideo: String?): AppResult<Unit>
    suspend fun deleteRecipe(id: Long): AppResult<Unit>
    suspend fun searchRecipes(query: String): AppResult<List<Recipe>>
}
