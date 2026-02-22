package pe.edu.cibertec.recetario.ui.recipes.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.edu.cibertec.recetario.databinding.ItemRecipeBinding
import pe.edu.cibertec.recetario.domain.model.Recipe

/**
 * Adaptador para el RecyclerView de Recetas.
 * Utiliza ListAdapter para gestionar la lista de forma eficiente mediante DiffUtil,
 * lo que permite actualizar solo los elementos que han cambiado.
 */
class RecipeAdapter(
    private val isLoggedIn: Boolean, // Indica si el usuario ha iniciado sesión
    private val onRecipeClick: (Recipe) -> Unit // Acción a ejecutar al pulsar una receta
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    // Crea la vista para cada elemento de la lista (infla el layout item_recipe.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    // Vincula los datos de una receta específica con su vista correspondiente
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder: Representa visualmente un solo ítem de la lista.
     */
    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            // Asigna los textos básicos
            binding.tvTitle.text = recipe.title
            binding.tvDescription.text = recipe.description

            // Si el usuario no está logueado, aplicamos un efecto de transparencia a la descripción
            if (!isLoggedIn) {
                binding.tvDescription.alpha = 0.7f
            } else {
                binding.tvDescription.alpha = 1.0f
            }

            // Carga la imagen de la receta desde internet usando Glide
            Glide.with(binding.ivRecipe.context)
                .load(recipe.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.ivRecipe)

            // Muestra un icono de video si la receta incluye contenido multimedia
            if (!recipe.videoUrl.isNullOrEmpty()) {
                binding.ivVideoBadge.visibility = View.VISIBLE
            } else {
                binding.ivVideoBadge.visibility = View.GONE
            }

            // Configura el clic en todo el elemento
            binding.root.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    /**
     * Clase auxiliar para calcular la diferencia entre dos listas de recetas.
     * Mejora el rendimiento al evitar recargar toda la lista si solo cambia un elemento.
     */
    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem == newItem
    }
}
