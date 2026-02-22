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

/**
 * Contenedor de Dependencias (Dependency Injection).
 * Esta clase centraliza la creación de todos los objetos importantes de la aplicación
 * (Retrofit, Repositorios, Casos de Uso) para que se compartan en toda la app.
 */
class AppContainer(private val context: Context) {

    // Gestor de sesión persistente (SharedPreferences)
    val sessionManager: SessionManager by lazy {
        SessionManager(context)
    }

    // Interceptor para ver las peticiones y respuestas en el Logcat (Depuración)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para añadir automáticamente el Token de seguridad a cada llamada a la API
    private val authInterceptor = AuthInterceptor(sessionManager)

    // Cliente HTTP configurado con los interceptores de seguridad y log
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    // Configuración centralizada de Retrofit para hablar con el servidor PHP
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin
            .build()
    }

    // Instancia única del servicio de API
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Mappers para transformar datos
    val recipeMapper = RecipeMapper()

    // --- Repositorios (Capa de Datos) ---
    
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, sessionManager)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepositoryImpl(apiService, recipeMapper)
    }

    val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(apiService, context.contentResolver)
    }

    // --- Casos de Uso (Capa de Dominio - Lógica de Negocio) ---
    // Cada uno representa una acción específica que el usuario puede realizar.

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
