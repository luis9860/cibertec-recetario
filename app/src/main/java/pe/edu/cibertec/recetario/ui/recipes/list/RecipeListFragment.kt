package pe.edu.cibertec.recetario.ui.recipes.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.R
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.databinding.FragmentRecipeListBinding
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory

/**
 * Fragmento encargado de mostrar la lista de recetas.
 * Puede funcionar en dos modos: Lista Pública (Home) o Mis Recetas (Perfil).
 */
class RecipeListFragment : Fragment() {

    // ViewBinding para acceder a los componentes del layout fragment_recipe_list.xml
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel mediante la factoría para inyectar dependencias
    private val viewModel: RecipeListViewModel by viewModels {
        val appContainer = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(appContainer)
    }

    private lateinit var adapter: RecipeAdapter // Adaptador para el RecyclerView
    private var isPrivateMode: Boolean = false  // Indica si estamos viendo "Mis Recetas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperamos el argumento para saber si mostrar recetas públicas o privadas
        isPrivateMode = arguments?.getBoolean("is_private") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()   // Configura la barra de búsqueda
        setupRecyclerView() // Configura la lista (RecyclerView)
        observeState()      // Escucha cambios en el estado del ViewModel
        
        // Carga las recetas inicialmente
        viewModel.loadRecipes(isPrivateMode)
    }

    /**
     * Configura el buscador para filtrar recetas en tiempo real.
     */
    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener {
            val query = it.toString()
            if (query.isNotEmpty()) {
                viewModel.searchRecipes(query) // Busca por texto
            } else {
                viewModel.loadRecipes(isPrivateMode) // Si está vacío, carga la lista normal
            }
        }
    }

    /**
     * Configura el RecyclerView y su adaptador.
     */
    private fun setupRecyclerView() {
        val sessionManager = (requireActivity().application as RecetarioApp).container.sessionManager
        val isLoggedIn = sessionManager.getToken() != null

        // Inicializa el adaptador con el evento de clic en cada receta
        adapter = RecipeAdapter(isLoggedIn) { recipe ->
            if (isLoggedIn) {
                // Si está logueado, va al detalle de la receta
                val bundle = bundleOf("recipe_id" to recipe.id)
                findNavController().navigate(R.id.recipeDetailFragment, bundle)
            } else {
                // Si no, invita a iniciar sesión
                Toast.makeText(requireContext(), "Inicia sesión para ver la receta completa", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        binding.rvRecipes.adapter = adapter
    }

    /**
     * Observa el estado del ViewModel y actualiza la lista y el cargador.
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Muestra/oculta el indicador de carga
                    binding.progressBar.isVisible = state.isLoading
                    // Actualiza los datos del adaptador
                    adapter.submitList(state.recipes)

                    // Muestra mensajes de error si ocurren
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpieza para evitar fugas de memoria
    }
}
