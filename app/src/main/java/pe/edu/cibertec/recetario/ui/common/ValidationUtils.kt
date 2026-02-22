package pe.edu.cibertec.recetario.ui.common

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

/**
 * Utilidades de Validación.
 * Contiene funciones estáticas para verificar que los datos ingresados por el usuario
 * sean correctos antes de enviarlos al servidor.
 * También gestiona la visualización de errores en los componentes Material Design (TextInputLayout).
 */
object ValidationUtils {

    /**
     * Valida que el correo no esté vacío y tenga un formato de email válido.
     */
    fun validateEmail(email: String, layout: TextInputLayout): Boolean {
        return when {
            email.isEmpty() -> {
                layout.error = "El correo es requerido"
                false
            }
            // Usa una expresión regular estándar de Android para validar el email
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                layout.error = "Correo no válido"
                false
            }
            else -> {
                layout.error = null // Quita el error si todo es correcto
                true
            }
        }
    }

    /**
     * Valida que la contraseña cumpla con un mínimo de seguridad (6 caracteres).
     */
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

    /**
     * Verifica que la confirmación de la contraseña sea idéntica a la contraseña original.
     */
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

    /**
     * Validación genérica para campos obligatorios (título, descripción, etc.).
     */
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
