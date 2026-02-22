package pe.edu.cibertec.recetario.data.repository

import pe.edu.cibertec.recetario.core.result.AppError
import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.data.mapper.RecipeMapper
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.domain.model.Recipe
import pe.edu.cibertec.recetario.data.remote.dto.UpdateRecipeRequest
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository
import org.json.JSONObject

/**
 * Implementación del Repositorio de Recetas.
 * Esta clase es el "puente" entre la API (Retrofit) y el resto de la aplicación.
 * Se encarga de realizar las llamadas de red y transformar los datos brutos (DTO)
 * en modelos que la aplicación entiende (Domain models).
 */
class RecipeRepositoryImpl(
    private val apiService: ApiService, // Servicio de Retrofit para las peticiones HTTP
    private val mapper: RecipeMapper     // Transformador de datos entre capas
) : RecipeRepository {

    /**
     * Obtiene la lista de recetas públicas.
     * Envuelve la llamada en safeApiCall para manejar errores de red de forma segura.
     */
    override suspend fun getPublicRecipes(): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.getPublicRecipes()
        if (response.isSuccessful) {
            // Si la respuesta es exitosa, mapeamos cada ítem de la lista
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    /**
     * Obtiene las recetas personales del usuario logueado.
     */
    override suspend fun getMyRecipes(): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.getMyRecipes()
        if (response.isSuccessful) {
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    /**
     * Envía una nueva receta al servidor.
     */
    override suspend fun createRecipe(recipe: Recipe): AppResult<Long> = safeApiCall {
        val response = apiService.createRecipe(mapper.toCreateRequest(recipe))
        if (response.isSuccessful && response.body()?.ok == true) {
            // Retorna el ID de la nueva receta creada
            response.body()?.id ?: -1L
        } else {
            throw Exception(response.body()?.message ?: "Error al crear receta")
        }
    }

    /**
     * Obtiene todos los detalles de una receta específica por su ID.
     */
    override suspend fun getRecipeDetail(id: Long): AppResult<Recipe> = safeApiCall {
        val response = apiService.getRecipeDetail(id)
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.ok == true) {
                mapper.toDomain(body.item) // Transforma el objeto DTO del servidor a objeto de Dominio
            } else {
                throw Exception("Error en la respuesta del servidor")
            }
        } else {
            // Si hay un error, intentamos extraer el mensaje de error del JSON
            val errorJson = response.errorBody()?.string()
            val message = try {
                JSONObject(errorJson ?: "").getString("error")
            } catch (e: Exception) {
                "Error ${response.code()}"
            }
            throw Exception(message)
        }
    }

    /**
     * Actualiza la información de una receta existente en la base de datos remota.
     */
    override suspend fun updateRecipe(id: Long, title: String, description: String, visibility: String, urlImagen: String?, urlVideo: String?): AppResult<Unit> = safeApiCall {
        val response = apiService.updateRecipe(UpdateRecipeRequest(id, title, description, visibility, urlImagen, urlVideo))
        if (response.isSuccessful && response.body()?.ok == true) {
            Unit // Operación exitosa sin retorno de datos
        } else {
            throw Exception(response.body()?.message ?: "Error al actualizar receta")
        }
    }

    /**
     * Elimina una receta permanentemente del servidor.
     */
    override suspend fun deleteRecipe(id: Long): AppResult<Unit> = safeApiCall {
        val response = apiService.deleteRecipe(mapOf("id" to id))
        if (response.isSuccessful && response.body()?.ok == true) {
            Unit
        } else {
            throw Exception("Error al eliminar receta")
        }
    }

    /**
     * Realiza una búsqueda de recetas filtrando por una palabra clave.
     */
    override suspend fun searchRecipes(query: String): AppResult<List<Recipe>> = safeApiCall {
        val response = apiService.searchRecipes(query)
        if (response.isSuccessful) {
            response.body()?.items?.map { mapper.toDomain(it) } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    /**
     * Función genérica para capturar cualquier error inesperado y devolverlo 
     * como un objeto AppResult.Error en lugar de que la aplicación se cierre.
     */
    private suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> {
        return try {
            AppResult.Success(call())
        } catch (e: Exception) {
            AppResult.Error(AppError.ServerError(e.message ?: "Unknown Error"))
        }
    }
}
