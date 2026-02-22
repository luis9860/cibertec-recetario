package pe.edu.cibertec.recetario.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.cibertec.recetario.di.AppContainer
import pe.edu.cibertec.recetario.ui.auth.LoginViewModel
import pe.edu.cibertec.recetario.ui.auth.RegisterViewModel
import pe.edu.cibertec.recetario.ui.recipes.add.AddRecipeViewModel
import pe.edu.cibertec.recetario.ui.recipes.detail.RecipeDetailViewModel
import pe.edu.cibertec.recetario.ui.recipes.edit.EditRecipeViewModel
import pe.edu.cibertec.recetario.ui.recipes.list.RecipeListViewModel

/**
 * Fábrica de ViewModels.
 * En Android, los ViewModels que requieren parámetros en su constructor (como los Casos de Uso)
 * no pueden ser instanciados directamente por el sistema. Esta clase se encarga de crearlos
 * e inyectarles las dependencias necesarias desde el AppContainer.
 */
class ViewModelFactory(
    private val container: AppContainer // Contenedor que tiene todas las dependencias de la app
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Instancia el ViewModel para la lista de recetas
            modelClass.isAssignableFrom(RecipeListViewModel::class.java) -> {
                RecipeListViewModel(
                    container.getPublicRecipesUseCase,
                    container.getMyRecipesUseCase,
                    container.searchRecipesUseCase
                ) as T
            }
            // Instancia el ViewModel para el inicio de sesión
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(container.loginUseCase) as T
            }
            // Instancia el ViewModel para el registro de usuarios
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(container.registerUseCase) as T
            }
            // Instancia el ViewModel para agregar nuevas recetas
            modelClass.isAssignableFrom(AddRecipeViewModel::class.java) -> {
                AddRecipeViewModel(container.createRecipeUseCase, container.uploadFileUseCase) as T
            }
            // Instancia el ViewModel para ver el detalle de una receta
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                RecipeDetailViewModel(container.getRecipeDetailUseCase, container.deleteRecipeUseCase) as T
            }
            // Instancia el ViewModel para editar recetas existentes
            modelClass.isAssignableFrom(EditRecipeViewModel::class.java) -> {
                EditRecipeViewModel(container.updateRecipeUseCase, container.uploadFileUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
