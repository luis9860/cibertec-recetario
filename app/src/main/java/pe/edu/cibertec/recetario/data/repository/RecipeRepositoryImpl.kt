package pe.edu.cibertec.recetario.data.repository

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.mapper.RecipeMapper
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.data.remote.dto.UpdateRecipeRequest
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository
import org.json.JSONObject

class RecipeRepositoryImpl(
    private val apiService: ApiService,
    private val mapper: RecipeMapper
) : RecipeRepository {

    override suspend fun getPublicRecipes(): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.getPublicRecipes()
        if (response.isSuccessful) {
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    override suspend fun getMyRecipes(): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.getMyRecipes()
        if (response.isSuccessful) {
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    override suspend fun createRecipe(recipe: Recipe): AppResult<Long> = safeApiCall {
        val response = apiService.createRecipe(mapper.toCreateRequest(recipe))
        if (response.isSuccessful && response.body()?.ok == true) {
            response.body()?.id ?: -1L
        } else {
            throw Exception(response.body()?.message ?: "Error al crear receta")
        }
    }

    override suspend fun getRecipeDetail(id: Long): AppResult<Recipe> = safeApiCall {
        val response = apiService.getRecipeDetail(id)
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.ok == true) {
                mapper.toDomain(body.item)
            } else {
                throw Exception("Error en la respuesta del servidor")
            }
        } else {
            val errorJson = response.errorBody()?.string()
            val message = try {
                JSONObject(errorJson ?: "").getString("error")
            } catch (e: Exception) {
                "Error ${response.code()}"
            }
            throw Exception(message)
        }
    }

    override suspend fun updateRecipe(id: Long, title: String, description: String, visibility: String, urlImagen: String?, urlVideo: String?): AppResult<Unit> = safeApiCall {
        val response = apiService.updateRecipe(UpdateRecipeRequest(id, title, description, visibility, urlImagen, urlVideo))
        if (response.isSuccessful && response.body()?.ok == true) {
            Unit
        } else {
            throw Exception(response.body()?.message ?: "Error al actualizar receta")
        }
    }

    override suspend fun deleteRecipe(id: Long): AppResult<Unit> = safeApiCall {
        val response = apiService.deleteRecipe(mapOf("id" to id))
        if (response.isSuccessful && response.body()?.ok == true) {
            Unit
        } else {
            throw Exception("Error al eliminar receta")
        }
    }

    override suspend fun searchRecipes(query: String): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.searchRecipes(query)
        if (response.isSuccessful) {
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> {
        return try {
            AppResult.Success(call())
        } catch (e: Exception) {
            AppResult.Error(AppError.ServerError(e.message ?: "Unknown Error"))
        }
    }
}
