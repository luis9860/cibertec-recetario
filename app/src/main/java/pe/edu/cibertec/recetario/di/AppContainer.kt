package pe.edu.cibertec.recetario.di

import pe.edu.cibertec.recetario.core.Config
import pe.edu.cibertec.recetario.data.local.SessionManager
import pe.edu.cibertec.recetario.data.remote.ApiService
import pe.edu.cibertec.recetario.data.repository.AuthRepositoryImpl
import pe.edu.cibertec.recetario.data.repository.RecipeRepositoryImpl
import pe.edu.cibertec.recetario.domain.repository.AuthRepository
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import pe.edu.cibertec.recetario.data.mapper.RecipeMapper
import pe.edu.cibertec.recetario.data.remote.AuthInterceptor
import pe.edu.cibertec.recetario.data.repository.MediaRepositoryImpl
import pe.edu.cibertec.recetario.domain.repository.MediaRepository
import pe.edu.cibertec.recetario.domain.usecase.*

class AppContainer(private val context: Context) {

    val sessionManager: SessionManager by lazy {
        SessionManager(context)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor(sessionManager)

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val recipeMapper = RecipeMapper()

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, sessionManager)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl(apiService, recipeMapper)
    }

    val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(apiService, context.contentResolver)
    }

    // Use Cases
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val registerUseCase by lazy { RegisterUseCase(authRepository) }
    val getPublicRecipesUseCase by lazy { GetPublicRecipesUseCase(recipeRepository) }
    val getMyRecipesUseCase by lazy { GetMyRecipesUseCase(recipeRepository) }
    val getRecipeDetailUseCase by lazy { GetRecipeDetailUseCase(recipeRepository) }
    val createRecipeUseCase by lazy { CreateRecipeUseCase(recipeRepository) }
    val uploadFileUseCase by lazy { UploadFileUseCase(mediaRepository) }
    val updateRecipeUseCase by lazy { UpdateRecipeUseCase(recipeRepository) }
    val deleteRecipeUseCase by lazy { DeleteRecipeUseCase(recipeRepository) }
    val searchRecipesUseCase by lazy { SearchRecipesUseCase(recipeRepository) }
}
