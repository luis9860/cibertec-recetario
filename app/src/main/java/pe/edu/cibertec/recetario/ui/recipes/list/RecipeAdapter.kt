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

class RecipeAdapter(
    private val isLoggedIn: Boolean,
    private val onRecipeClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.tvTitle.text = recipe.title
            binding.tvDescription.text = recipe.description

            if (!isLoggedIn) {
                binding.tvDescription.alpha = 0.7f
            } else {
                binding.tvDescription.alpha = 1.0f
            }

            // Log para debug (puedes verlo en Logcat)
            android.util.Log.d("RECIPE_IMG", "Cargando imagen: ${recipe.imageUrl}")
            android.util.Log.d("RECIPE_VID", "Cargando video: ${recipe.videoUrl}")

            // Cargar Imagen
            Glide.with(binding.ivRecipe.context)
                .load(recipe.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.ivRecipe)

            // Cargar miniatura de Video si existe
            if (!recipe.videoUrl.isNullOrEmpty()) {
                binding.ivVideoBadge.visibility = View.VISIBLE
                // Opcional: Podrías usar otro ImageView para la miniatura del video si actualizas el XML
            } else {
                binding.ivVideoBadge.visibility = View.GONE
            }

            binding.root.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem == newItem
    }
}
