package pe.edu.cibertec.recetario.domain.repository

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.model.Recipe

/**
 * Interfaz del Repositorio de Recetas.
 * Define las reglas de negocio sobre cómo se deben obtener y manipular las recetas.
 * Esta interfaz es implementada en la capa de datos (RecipeRepositoryImpl).
 */
interface RecipeRepository {
    // Obtiene el listado de todas las recetas marcadas como públicas
    suspend fun getPublicRecipes(): AppResult<List<Recipe>>
    
    // Obtiene únicamente las recetas que pertenecen al usuario actual
    suspend fun getMyRecipes(): AppResult<List<Recipe>>
    
    // Crea una nueva receta en el sistema
    suspend fun createRecipe(recipe: Recipe): AppResult<Long>
    
    // Obtiene la información completa de una receta específica
    suspend fun getRecipeDetail(id: Long): AppResult<Recipe>
    
    // Actualiza los datos de una receta existente
    suspend fun updateRecipe(id: Long, title: String, description: String, visibility: String, urlImagen: String?, urlVideo: String?): AppResult<Unit>
    
    // Elimina una receta permanentemente
    suspend fun deleteRecipe(id: Long): AppResult<Unit>
    
    // Realiza una búsqueda filtrada de recetas
    suspend fun searchRecipes(query: String): AppResult<List<Recipe>>
}
