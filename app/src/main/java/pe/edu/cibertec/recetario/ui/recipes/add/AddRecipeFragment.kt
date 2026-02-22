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

class AddRecipeFragment : Fragment() {

    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddRecipeViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivPreview.isVisible = true
            binding.ivPreview.setImageURI(it)
            viewModel.onImageSelected(it)
        }
    }

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { validateAndProcessVideo(it) }
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
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnPickVideo.setOnClickListener { pickVideo.launch("video/mp4") }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val visibility = if (binding.rbPublic.isChecked) "public" else "private"

            if (title.isBlank()) {
                binding.etTitle.error = "El título es requerido"
                return@setOnClickListener
            }

            viewModel.createRecipe(title, description, visibility)
        }
    }

    private fun validateAndProcessVideo(uri: Uri) {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
                val name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                
                if (size > 400 * 1024 * 1024) {
                    Toast.makeText(context, "Video demasiado grande (Máx 400MB)", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvVideoInfo.isVisible = true
                    binding.tvVideoInfo.text = "Video seleccionado: $name (${size / (1024 * 1024)} MB)"
                    viewModel.onVideoSelected(uri, name)
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnSave.isEnabled = !state.isLoading
                    binding.btnPickImage.isEnabled = !state.isLoading
                    binding.btnPickVideo.isEnabled = !state.isLoading

                    binding.tvUploadStatus.isVisible = state.uploadStatus != null
                    binding.tvUploadStatus.text = state.uploadStatus

                    binding.progressImage.isVisible = state.imageProgress > 0 && state.imageProgress < 100
                    binding.progressImage.progress = state.imageProgress

                    binding.progressVideo.isVisible = state.videoProgress > 0 && state.videoProgress < 100
                    binding.progressVideo.progress = state.videoProgress

                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Receta creada con éxito", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }

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
