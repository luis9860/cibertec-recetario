package pe.edu.cibertec.recetario

import android.app.Application
import pe.edu.cibertec.recetario.di.AppContainer

/**
 * Clase Application personalizada.
 * Se ejecuta antes de cualquier otra parte de la aplicación (incluso antes de MainActivity).
 * Es el lugar ideal para inicializar objetos globales y la Inyección de Dependencias.
 */
class RecetarioApp : Application() {

    // Contenedor global que guarda las dependencias (Retrofit, Repositorios, etc.)
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        
        // Inicializamos el contenedor de dependencias al arrancar la app
        // para que esté disponible en todos los ViewModels.
        container = AppContainer(this)
    }
}
