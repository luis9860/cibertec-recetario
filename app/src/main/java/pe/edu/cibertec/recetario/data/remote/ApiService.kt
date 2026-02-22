package pe.edu.cibertec.recetario.data.remote

import okhttp3.MultipartBody
import pe.edu.cibertec.recetario.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth_register.php")
    suspend fun register(@Body request: Map<String, String>): Response<Map<String, Any>>

    @POST("auth_login.php")
    suspend fun login(@Body request: Map<String, String>): Response<Map<String, Any>>

    @POST("auth_logout.php")
    suspend fun logout(): Response<Map<String, Any>>

    @GET("recipes_public_list.php")
    suspend fun getPublicRecipes(): Response<RecipeListResponse>

    @GET("recipes_list.php")
    suspend fun getMyRecipes(): Response<RecipeListResponse>

    @POST("recipes_create.php")
    suspend fun createRecipe(@Body recipe: CreateRecipeRequest): Response<RecipeResponse>

    @GET("recipes_public_detail.php")
    suspend fun getRecipeDetail(@Query("id") id: Long): Response<RecipeDetailResponse>

    @POST("recipes_update.php")
    suspend fun updateRecipe(@Body recipe: UpdateRecipeRequest): Response<RecipeResponse>

    @POST("recipes_delete.php")
    suspend fun deleteRecipe(@Body body: Map<String, Long>): Response<GenericResponse>

    @GET("recipes_search.php")
    suspend fun searchRecipes(@Query("query") query: String): Response<RecipeListResponse>

    @Multipart
    @POST("upload.php")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}
