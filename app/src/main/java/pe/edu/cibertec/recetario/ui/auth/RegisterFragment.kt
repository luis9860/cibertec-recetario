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

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            if (validateFields()) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.register(email, password)
            }
        }

        binding.btnGoLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun validateFields(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val isEmailValid = ValidationUtils.validateEmail(email, binding.layoutEmail)
        val isPasswordValid = ValidationUtils.validatePassword(password, binding.layoutPassword)
        val isConfirmPasswordValid = ValidationUtils.validateConfirmPassword(password, confirmPassword, binding.layoutPassword2)

        return isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnRegister.isEnabled = !state.isLoading

                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
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
