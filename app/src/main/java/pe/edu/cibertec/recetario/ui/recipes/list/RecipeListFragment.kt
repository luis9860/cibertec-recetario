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

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeListViewModel by viewModels {
        val appContainer = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(appContainer)
    }

    private lateinit var adapter: RecipeAdapter
    private var isPrivateMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setupSearchView()
        setupRecyclerView()
        observeState()
        viewModel.loadRecipes(isPrivateMode)
    }

    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener {
            val query = it.toString()
            if (query.isNotEmpty()) {
                viewModel.searchRecipes(query)
            } else {
                viewModel.loadRecipes(isPrivateMode)
            }
        }
    }

    private fun setupRecyclerView() {
        val sessionManager = (requireActivity().application as RecetarioApp).container.sessionManager
        val isLoggedIn = sessionManager.getToken() != null

        adapter = RecipeAdapter(isLoggedIn) { recipe ->
            if (isLoggedIn) {
                val bundle = bundleOf("recipe_id" to recipe.id)
                findNavController().navigate(R.id.recipeDetailFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Inicia sesión para ver la receta completa", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        binding.rvRecipes.adapter = adapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    adapter.submitList(state.recipes)

                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
