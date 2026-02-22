package pe.edu.cibertec.recetario.ui.auth

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pe.edu.cibertec.recetario.MainActivity
import pe.edu.cibertec.recetario.R
import pe.edu.cibertec.recetario.RecetarioApp
import pe.edu.cibertec.recetario.databinding.FragmentLoginBinding
import pe.edu.cibertec.recetario.ui.common.ValidationUtils
import pe.edu.cibertec.recetario.ui.common.ViewModelFactory

/**
 * Fragmento de Inicio de Sesión (Login).
 * Permite a los usuarios existentes acceder a su cuenta validando sus credenciales.
 */
class LoginFragment : Fragment() {

    // ViewBinding para acceder a los campos del layout fragment_login.xml
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel encargado de procesar la autenticación
    private val viewModel: LoginViewModel by viewModels {
        val container = (requireActivity().application as RecetarioApp).container
        ViewModelFactory(container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners() // Configura los clics en botones
        observeState()   // Escucha los cambios de estado (carga, éxito, error)
    }

    /**
     * Configura los botones de la interfaz.
     */
    private fun setupListeners() {
        // Botón para iniciar sesión
        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.login(email, password)
            }
        }

        // Enlace para ir a la pantalla de Registro si no tiene cuenta
        binding.btnGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    /**
     * Valida que el formato del correo y la contraseña sean correctos antes de enviar al servidor.
     */
    private fun validateFields(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val isEmailValid = ValidationUtils.validateEmail(email, binding.layoutEmail)
        val isPasswordValid = ValidationUtils.validatePassword(password, binding.layoutPassword)

        return isEmailValid && isPasswordValid
    }

    /**
     * Reacciona a los cambios en el estado de autenticación.
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Muestra/oculta el indicador de carga
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnLogin.isEnabled = !state.isLoading

                    // Si el login es exitoso, reinicia la app para aplicar el nuevo tema y mostrar el perfil
                    if (state.isSuccess) {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        // Limpia la pila de actividades para evitar que el usuario regrese al login con el botón atrás
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    // Muestra errores si las credenciales son incorrectas
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita fugas de memoria al destruir el fragmento
    }
}
