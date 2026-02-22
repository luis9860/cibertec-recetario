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

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecipeListViewModel::class.java) -> {
                RecipeListViewModel(
                    container.getPublicRecipesUseCase,
                    container.getMyRecipesUseCase,
                    container.searchRecipesUseCase
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(container.loginUseCase) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(container.registerUseCase) as T
            }
            modelClass.isAssignableFrom(AddRecipeViewModel::class.java) -> {
                AddRecipeViewModel(container.createRecipeUseCase, container.uploadFileUseCase) as T
            }
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                RecipeDetailViewModel(container.getRecipeDetailUseCase, container.deleteRecipeUseCase) as T
            }
            modelClass.isAssignableFrom(EditRecipeViewModel::class.java) -> {
                EditRecipeViewModel(container.updateRecipeUseCase, container.uploadFileUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
