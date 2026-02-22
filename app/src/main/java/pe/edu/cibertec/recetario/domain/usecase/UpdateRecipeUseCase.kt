package pe.edu.cibertec.recetario.domain.usecase

import pe.edu.cibertec.recetario.core.result.AppResult
import pe.edu.cibertec.recetario.domain.repository.RecipeRepository

/**
 * Caso de Uso para actualizar una receta.
 * Coordina la modificación de los datos de una receta existente en el servidor.
 */
class UpdateRecipeUseCase(private val recipeRepository: RecipeRepository) {
    /**
     * Ejecuta la actualización de la receta.
     * @param id Identificador de la receta a modificar.
     * @param title Nuevo título de la receta.
     * @param description Nuevo procedimiento de la receta.
     * @param visibility Nueva visibilidad (public o private).
     * @param urlImagen Nueva URL de imagen (opcional).
     * @param urlVideo Nueva URL de video (opcional).
     * @return El resultado de la operación (éxito o error).
     */
    suspend operator fun invoke(
        id: Long,
        title: String,
        description: String,
        visibility: String,
        urlImagen: String?,
        urlVideo: String?
    ): AppResult<Unit> = recipeRepository.updateRecipe(id, title, description, visibility, urlImagen, urlVideo)
}
