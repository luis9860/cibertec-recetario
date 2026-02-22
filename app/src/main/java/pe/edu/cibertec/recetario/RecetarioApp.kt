package pe.edu.cibertec.recetario

import android.app.Application
import pe.edu.cibertec.recetario.di.AppContainer

class RecetarioApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
