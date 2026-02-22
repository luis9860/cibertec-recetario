package pe.edu.cibertec.recetario.ui.recipes.detail

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.edu.cibertec.recetario.R
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.data.local.SessionManager
import pe.edu.cibertec.recetario.databinding.FragmentRecipeDetailBinding
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private var player: ExoPlayer? = null
    private var videoUrl: String? = null
    private var isVerticalVideo = false
    private var isManualFullscreen = false

    private val viewModel: RecipeDetailViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getLong("recipe_id") ?: -1L
        if (recipeId != -1L) {
            viewModel.loadRecipe(recipeId)
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.detailProgressBar.isVisible = state.isLoading

                    state.recipe?.let { recipe ->
                        binding.tvDetailTitle.text = recipe.title
                        binding.tvDetailDescription.text = recipe.description
                        binding.tvDetailAuthor.text = "Autor: ${recipe.authorEmail ?: "Anónimo"}"

                        Glide.with(this@RecipeDetailFragment)
                            .load(recipe.imageUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivRecipeDetail)

                        // Botones editar/eliminar solo si es del usuario
                        binding.layoutActions.isVisible = recipe.isMine
                        if (recipe.isMine) {
                            binding.btnEdit.setOnClickListener {
                                val bundle = bundleOf(
                                    "recipe_id" to recipe.id,
                                    "recipe_title" to recipe.title,
                                    "recipe_description" to recipe.description,
                                    "recipe_visibility" to recipe.visibility,
                                    "recipe_image_url" to recipe.imageUrl,
                                    "recipe_video_url" to recipe.videoUrl
                                )
                                findNavController().navigate(R.id.action_recipeDetailFragment_to_editRecipeFragment, bundle)
                            }
                            binding.btnDelete.setOnClickListener {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Eliminar receta")
                                    .setMessage("¿Estás seguro de que deseas eliminar \"${recipe.title}\"?")
                                    .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteRecipe() }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        }

                        // Solo mostramos el botón, no iniciamos el video automáticamente
                        if (!recipe.videoUrl.isNullOrBlank()) {
                            binding.btnWatchVideo.isVisible = true
                            binding.btnWatchVideo.setOnClickListener {
                                videoUrl = recipe.videoUrl
                                binding.btnWatchVideo.isVisible = false
                                detectVideoOrientation(recipe.videoUrl!!)
                            }
                        } else {
                            binding.btnWatchVideo.isVisible = false
                        }
                    }

                    if (state.isDeleted) {
                        Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun detectVideoOrientation(url: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.videoProgressBar.isVisible = true
            val isVertical = withContext(Dispatchers.IO) {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(url, HashMap<String, String>())
                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
                    val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
                    retriever.release()
                    if (rotation == 90 || rotation == 270) width > height else height > width
                } catch (e: Exception) {
                    false
                }
            }
            isVerticalVideo = isVertical
            setupPlayer(url)
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer(url: String) {
        val token = sessionManager.getToken() ?: ""

        binding.videoContainer.isVisible = true
        binding.ivRecipeDetail.isVisible = false

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))

        player = ExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        binding.playerView.apply {
            player = this@RecipeDetailFragment.player
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

            setFullscreenButtonClickListener {
                if (isVerticalVideo) {
                    toggleVerticalFullscreen()
                } else {
                    openFullScreenActivity()
                }
            }
        }

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        binding.videoProgressBar.isVisible = false
    }

    private fun toggleVerticalFullscreen() {
        isManualFullscreen = !isManualFullscreen
        applyVerticalFullscreenUI(isManualFullscreen)
    }

    private fun applyVerticalFullscreenUI(full: Boolean) {
        val activity = requireActivity() as AppCompatActivity
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (full) {
            bottomNav?.isVisible = false
            hideSystemUI(true)
            binding.detailContentLayout.setPadding(0, 0, 0, 0)

            val params = binding.videoContainer.layoutParams as LinearLayout.LayoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.videoContainer.layoutParams = params

            binding.tvDetailTitle.isVisible = false
            binding.tvDetailDescription.isVisible = false
            binding.tvDetailAuthor.isVisible = false
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            bottomNav?.isVisible = true
            hideSystemUI(false)

            val density = resources.displayMetrics.density
            val padding = (16 * density).toInt()
            binding.detailContentLayout.setPadding(padding, padding, padding, padding)

            val params = binding.videoContainer.layoutParams as LinearLayout.LayoutParams
            params.height = (250 * density).toInt()
            binding.videoContainer.layoutParams = params

            binding.tvDetailTitle.isVisible = true
            binding.tvDetailDescription.isVisible = true
            binding.tvDetailAuthor.isVisible = true
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    private fun hideSystemUI(hide: Boolean) {
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, !hide)
        if (hide) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun openFullScreenActivity() {
        val intent = Intent(requireContext(), FullScreenVideoActivity::class.java).apply {
            putExtra("video_url", videoUrl)
            putExtra("token", sessionManager.getToken())
        }
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        applyVerticalFullscreenUI(false)
        player?.release()
        player = null
        _binding = null
    }
}
