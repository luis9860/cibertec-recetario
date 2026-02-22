package pe.edu.cibertec.recetario.ui.recipes.add

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.databinding.FragmentAddRecipeBinding
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory

/**
 * Fragmento para agregar una nueva receta.
 * Permite al usuario ingresar título, descripción, visibilidad y seleccionar una imagen y un video.
 */
class AddRecipeFragment : Fragment() {

    // ViewBinding para acceder a los componentes del layout fragment_add_recipe.xml
    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel mediante una factoría personalizada
    private val viewModel: AddRecipeViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    // Lanzador para seleccionar una imagen desde la galería
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivPreview.isVisible = true
            binding.ivPreview.setImageURI(it)
            viewModel.onImageSelected(it) // Notifica al ViewModel la imagen seleccionada
        }
    }

    // Lanzador para seleccionar un video desde la galería
    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { validateAndProcessVideo(it) } // Valida el tamaño antes de aceptar el video
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners() // Configura los eventos de clic
        observeState()   // Escucha los cambios en el ViewModel
    }

    /**
     * Configura los botones de la interfaz.
     */
    private fun setupListeners() {
        // Abrir selector de imagen
        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        
        // Abrir selector de video
        binding.btnPickVideo.setOnClickListener { pickVideo.launch("video/mp4") }

        // Botón Guardar: Valida y envía los datos al ViewModel
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val visibility = if (binding.rbPublic.isChecked) "public" else "private"

            if (title.isBlank()) {
                binding.etTitle.error = "El título es requerido"
                return@setOnClickListener
            }

            // Inicia el proceso de creación (subida de archivos + registro)
            viewModel.createRecipe(title, description, visibility)
        }
    }

    /**
     * Verifica que el video seleccionado no exceda los 400MB.
     */
    private fun validateAndProcessVideo(uri: Uri) {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
                val name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                
                if (size > 400 * 1024 * 1024) { // Límite de 400MB
                    Toast.makeText(context, "Video demasiado grande (Máx 400MB)", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvVideoInfo.isVisible = true
                    binding.tvVideoInfo.text = "Video seleccionado: $name (${size / (1024 * 1024)} MB)"
                    viewModel.onVideoSelected(uri, name)
                }
            }
        }
    }

    /**
     * Observa el estado del ViewModel para actualizar la UI (progresos, mensajes, éxito).
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Muestra el progreso de carga general
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnSave.isEnabled = !state.isLoading
                    binding.btnPickImage.isEnabled = !state.isLoading
                    binding.btnPickVideo.isEnabled = !state.isLoading

                    // Muestra el estado textual de la subida (Ej: "Subiendo imagen...")
                    binding.tvUploadStatus.isVisible = state.uploadStatus != null
                    binding.tvUploadStatus.text = state.uploadStatus

                    // Barras de progreso individuales para imagen y video
                    binding.progressImage.isVisible = state.imageProgress > 0 && state.imageProgress < 100
                    binding.progressImage.progress = state.imageProgress

                    binding.progressVideo.isVisible = state.videoProgress > 0 && state.videoProgress < 100
                    binding.progressVideo.progress = state.videoProgress

                    // Si la receta se creó con éxito, informa y vuelve atrás
                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Receta creada con éxito", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }

                    // Errores del servidor o de red
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
