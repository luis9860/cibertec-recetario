package pe.edu.cibertec.recetario.ui.auth

import android.os.Bundle
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
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.databinding.FragmentRegisterBinding
import pe.edu.cibertec.recetario.ui.common.ValidationUtils
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory

/**
 * Fragmento de Registro de Usuario.
 * Permite a nuevos usuarios crear una cuenta en el sistema validando su correo y contraseña.
 */
class RegisterFragment : Fragment() {

    // ViewBinding para interactuar con los elementos del diseño XML fragment_register.xml
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel mediante una factoría para inyectar dependencias
    private val viewModel: RegisterViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners() // Configura las acciones de los botones
        observeState()   // Escucha cambios en el estado de registro
    }

    /**
     * Configura los eventos de clic para los botones de la interfaz.
     */
    private fun setupListeners() {
        // Al pulsar el botón de registro, se validan los campos y se llama al ViewModel
        binding.btnRegister.setOnClickListener {
            if (validateFields()) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.register(email, password)
            }
        }

        // Botón para regresar a la pantalla de Login
        binding.btnGoLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Valida que el email tenga formato correcto, la contraseña cumpla los requisitos
     * y que ambas contraseñas coincidan.
     */
    private fun validateFields(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Uso de utilidades comunes para mostrar errores visuales en los campos
        val isEmailValid = ValidationUtils.validateEmail(email, binding.layoutEmail)
        val isPasswordValid = ValidationUtils.validatePassword(password, binding.layoutPassword)
        val isConfirmPasswordValid = ValidationUtils.validateConfirmPassword(password, confirmPassword, binding.layoutPassword2)

        return isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    /**
     * Observa el flujo de estados emitido por el ViewModel para reaccionar a la carga,
     * el éxito o los errores del registro.
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Muestra u oculta la barra de progreso y bloquea el botón mientras carga
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnRegister.isEnabled = !state.isLoading

                    // Si el registro es exitoso, informa al usuario y vuelve al login
                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }

                    // Muestra mensajes de error provenientes del servidor o de la lógica de negocio
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
