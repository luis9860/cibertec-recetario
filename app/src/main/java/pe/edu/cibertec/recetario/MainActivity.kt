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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val THEME_DEBUG_TAG = "THEME_APPLIER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = (application as RecetarioApp).container.sessionManager

        if (sessionManager.isUserLoggedIn()) {
            Log.d(THEME_DEBUG_TAG, "Usuario LOGUEADO. Aplicando tema 'LoggedIn'.")
            setTheme(R.style.Theme_Recetario_LoggedIn)
        } else {
            Log.d(THEME_DEBUG_TAG, "Usuario NO logueado. Aplicando tema por defecto.")
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_profile, R.id.nav_add),
            binding.drawerLayout
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)

        updateDrawerMenu()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_logout) {
                logout()
                true
            } else {
                val handled = handleNavigation(item.itemId)
                if (handled) binding.drawerLayout.closeDrawer(GravityCompat.START)
                handled
            }
        }

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

    private fun updateDrawerMenu() {
        val menu = binding.navView.menu
        val isLoggedIn = sessionManager.isUserLoggedIn()
        menu.findItem(R.id.loginFragment).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
    }

    private fun logout() {
        sessionManager.clearSession()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        recreate()
    }

    private fun showLoginRequired(action: String) {
        Toast.makeText(this, "Inicia sesión para $action", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.loginFragment)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
