package pe.edu.cibertec.recetario.ui.recipes.edit

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.databinding.FragmentEditRecipeBinding
import pe.edu.cibertec.recetario.ui.common.ValidationUtils
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory

/**
 * Pantalla para editar una receta existente.
 * Permite modificar los datos de la receta, incluyendo la actualización de imagen y video.
 */
class EditRecipeFragment : Fragment() {

    // Acceso a las vistas mediante ViewBinding
    private var _binding: FragmentEditRecipeBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel encargado de la lógica de edición
    private val viewModel: EditRecipeViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    private var recipeId: Long = -1L
    private var currentImageUrl: String? = null
    private var currentVideoUrl: String? = null

    // Lanzador para seleccionar una imagen desde la galería
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivPreview.isVisible = true
            binding.ivPreview.setImageURI(it)
            viewModel.onImageSelected(it) // Informa al ViewModel sobre la nueva imagen seleccionada
        }
    }

    // Lanzador para seleccionar un video desde la galería
    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { validateAndProcessVideo(it) } // Valida el tamaño antes de procesarlo
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera los datos de la receta pasados como argumentos desde la pantalla anterior
        recipeId = arguments?.getLong("recipe_id") ?: -1L
        currentImageUrl = arguments?.getString("recipe_image_url")
        currentVideoUrl = arguments?.getString("recipe_video_url")

        // Rellena los campos con la información actual de la receta
        binding.etTitle.setText(arguments?.getString("recipe_title") ?: "")
        binding.etDescription.setText(arguments?.getString("recipe_description") ?: "")
        val visibility = arguments?.getString("recipe_visibility") ?: "public"
        if (visibility == "private") binding.rbPrivate.isChecked = true else binding.rbPublic.isChecked = true

        setupListeners()
        observeState()
    }

    /**
     * Configura los botones de la interfaz: selección de archivos y guardado.
     */
    private fun setupListeners() {
        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnPickVideo.setOnClickListener { pickVideo.launch("video/mp4") }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()

            // Valida que los campos obligatorios no estén vacíos
            val isTitleValid = ValidationUtils.validateNotEmpty(title, binding.layoutTitle, "título")
            val isDescriptionValid = ValidationUtils.validateNotEmpty(description, binding.layoutDescription, "descripción")

            if (isTitleValid && isDescriptionValid) {
                val visibility = if (binding.rbPublic.isChecked) "public" else "private"
                // Solicita al ViewModel que actualice la receta en el servidor
                viewModel.updateRecipe(recipeId, title.trim(), description.trim(), visibility, currentImageUrl, currentVideoUrl)
            }
        }
    }

    /**
     * Verifica que el video seleccionado no supere el límite de tamaño permitido (400MB).
     */
    private fun validateAndProcessVideo(uri: Uri) {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
                val name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                if (size > 400 * 1024 * 1024) { // 400MB en bytes
                    Toast.makeText(context, "Video demasiado grande (Máx 400MB)", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvVideoInfo.isVisible = true
                    binding.tvVideoInfo.text = "Video: $name (${size / (1024 * 1024)} MB)"
                    viewModel.onVideoSelected(uri) // Informa al ViewModel sobre el video seleccionado
                }
            }
        }
    }

    /**
     * Escucha los cambios de estado en el ViewModel para actualizar la interfaz (progreso, éxito, error).
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Muestra u oculta elementos según el estado de carga
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnSave.isEnabled = !state.isLoading
                    binding.btnPickImage.isEnabled = !state.isLoading
                    binding.btnPickVideo.isEnabled = !state.isLoading

                    // Muestra el estado y progreso de la subida de archivos
                    binding.tvUploadStatus.isVisible = state.uploadStatus != null
                    binding.tvUploadStatus.text = state.uploadStatus

                    binding.progressImage.isVisible = state.imageProgress > 0 && state.imageProgress < 100
                    binding.progressImage.progress = state.imageProgress

                    binding.progressVideo.isVisible = state.videoProgress > 0 && state.videoProgress < 100
                    binding.progressVideo.progress = state.videoProgress

                    // Si la operación fue exitosa, informa al usuario y cierra la pantalla
                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Receta actualizada", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }

                    // Muestra mensajes de error si ocurren
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpia el binding para evitar fugas de memoria
    }
}
