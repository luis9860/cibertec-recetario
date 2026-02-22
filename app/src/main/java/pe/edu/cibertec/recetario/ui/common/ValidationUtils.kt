package pe.edu.cibertec.recetario.ui.common

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object ValidationUtils {

    fun validateEmail(email: String, layout: TextInputLayout): Boolean {
        return when {
            email.isEmpty() -> {
                layout.error = "El correo es requerido"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                layout.error = "Correo no válido"
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    fun validatePassword(password: String, layout: TextInputLayout): Boolean {
        return when {
            password.isEmpty() -> {
                layout.error = "La contraseña es requerida"
                false
            }
            password.length < 6 -> {
                layout.error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    fun validateConfirmPassword(password: String, confirm: String, layout: TextInputLayout): Boolean {
        return when {
            confirm.isEmpty() -> {
                layout.error = "Confirme la contraseña"
                false
            }
            password != confirm -> {
                layout.error = "Las contraseñas no coinciden"
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    fun validateNotEmpty(text: String, layout: TextInputLayout, fieldName: String): Boolean {
        return if (text.isBlank()) {
            layout.error = "El campo $fieldName es requerido"
            false
        } else {
            layout.error = null
            true
        }
    }
}
