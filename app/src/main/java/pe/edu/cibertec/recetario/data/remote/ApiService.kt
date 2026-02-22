package pe.edu.cibertec.recetario.data.remote

import okhttp3.MultipartBody
import pe.edu.cibertec.recetario.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit que define los puntos de conexión (endpoints) de la API REST.
 * Cada función representa una llamada al servidor PHP.
 */
interface ApiService {

    // Registro de nuevos usuarios
    @POST("auth_register.php")
    suspend fun register(@Body request: Map<String, String>): Response<Map<String, Any>>

    // Inicio de sesión (Login)
    @POST("auth_login.php")
    suspend fun login(@Body request: Map<String, String>): Response<Map<String, Any>>

    // Cierre de sesión en el servidor
    @POST("auth_logout.php")
    suspend fun logout(): Response<Map<String, Any>>

    // Obtiene todas las recetas públicas de la comunidad
    @GET("recipes_public_list.php")
    suspend fun getPublicRecipes(): Response<RecipeListResponse>

    // Obtiene solo las recetas creadas por el usuario logueado
    @GET("recipes_list.php")
    suspend fun getMyRecipes(): Response<RecipeListResponse>

    // Envía los datos para crear una nueva receta
    @POST("recipes_create.php")
    suspend fun createRecipe(@Body recipe: CreateRecipeRequest): Response<RecipeResponse>

    // Obtiene la información detallada de una sola receta por su ID
    @GET("recipes_public_detail.php")
    suspend fun getRecipeDetail(@Query("id") id: Long): Response<RecipeDetailResponse>

    // Actualiza los datos de una receta existente
    @POST("recipes_update.php")
    suspend fun updateRecipe(@Body recipe: UpdateRecipeRequest): Response<RecipeResponse>

    // Elimina una receta del servidor
    @POST("recipes_delete.php")
    suspend fun deleteRecipe(@Body body: Map<String, Long>): Response<GenericResponse>

    // Busca recetas por título o ingredientes
    @GET("recipes_search.php")
    suspend fun searchRecipes(@Query("query") query: String): Response<RecipeListResponse>

    // Sube archivos (Imagen/Video) mediante peticiones Multipart
    @Multipart
    @POST("upload.php")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}
