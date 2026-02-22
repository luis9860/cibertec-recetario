package pe.edu.cibertec.recetario

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import pe.edu.cibertec.recetario.data.local.SessionManager
import pe.edu.cibertec.recetario.databinding.ActivityMainBinding

/**
 * Actividad Principal de la aplicación.
 * Actúa como el contenedor principal (Host) para todos los fragmentos
 * y gestiona la navegación superior, inferior y lateral.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val THEME_DEBUG_TAG = "THEME_APPLIER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inicializa el gestor de sesiones para verificar si el usuario está logueado
        sessionManager = (application as RecetarioApp).container.sessionManager

        // Aplica un tema diferente si el usuario ha iniciado sesión
        if (sessionManager.isUserLoggedIn()) {
            Log.d(THEME_DEBUG_TAG, "Usuario LOGUEADO. Aplicando tema 'LoggedIn'.")
            setTheme(R.style.Theme_Recetario_LoggedIn)
        } else {
            Log.d(THEME_DEBUG_TAG, "Usuario NO logueado. Aplicando tema por defecto.")
        }

        super.onCreate(savedInstanceState)

        // Configura el ViewBinding para acceder a los componentes del layout activity_main.xml
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura la barra de herramientas (Toolbar) como la ActionBar de la actividad
        setSupportActionBar(binding.toolbar)

        // Configura el NavController que gestiona el intercambio de fragmentos
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define las pestañas principales para que la Toolbar muestre el icono del menú lateral (Drawer)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_profile, R.id.nav_add),
            binding.drawerLayout
        )
        
        // Vincula el NavController con la Toolbar, el Menú Lateral y la Navegación Inferior
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)

        // Actualiza las opciones del menú lateral según el estado de la sesión
        updateDrawerMenu()

        // Escucha los clics en la barra de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        // Escucha los clics en el menú lateral (Drawer)
        binding.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_logout) {
                logout() // Cierra la sesión si se pulsa "Salir"
                true
            } else {
                val handled = handleNavigation(item.itemId)
                if (handled) binding.drawerLayout.closeDrawer(GravityCompat.START)
                handled
            }
        }

        // Detecta cambios de pantalla para ocultar/mostrar barras (ej: ocultar en Login)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateDrawerMenu()
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Lógica centralizada para manejar la navegación y proteger rutas privadas.
     */
    private fun handleNavigation(itemId: Int): Boolean {
        val isLoggedIn = sessionManager.isUserLoggedIn()

        return when (itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
                true
            }
            R.id.nav_add -> {
                if (isLoggedIn) {
                    navController.navigate(R.id.nav_add)
                } else {
                    showLoginRequired("agregar recetas")
                }
                true
            }
            R.id.nav_profile -> {
                if (isLoggedIn) {
                    navController.navigate(R.id.nav_profile)
                } else {
                    showLoginRequired("ver tus recetas")
                }
                true
            }
            R.id.loginFragment -> {
                navController.navigate(R.id.loginFragment)
                true
            }
            else -> false
        }
    }

    /**
     * Muestra u oculta las opciones de Login/Logout en el menú lateral según la sesión.
     */
    private fun updateDrawerMenu() {
        val menu = binding.navView.menu
        val isLoggedIn = sessionManager.isUserLoggedIn()
        menu.findItem(R.id.loginFragment).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
    }

    /**
     * Limpia los datos de sesión y reinicia la actividad para aplicar cambios.
     */
    private fun logout() {
        sessionManager.clearSession()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        recreate() // Recarga la Activity para resetear el estado y el tema
    }

    /**
     * Redirige al Login si el usuario intenta acceder a una función protegida.
     */
    private fun showLoginRequired(action: String) {
        Toast.makeText(this, "Inicia sesión para $action", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.loginFragment)
    }

    /**
     * Maneja la pulsación del botón atrás: si el menú lateral está abierto, lo cierra.
     */
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
