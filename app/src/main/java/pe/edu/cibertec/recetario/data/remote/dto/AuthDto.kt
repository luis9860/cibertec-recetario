package pe.edu.cibertec.recetario.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("token") val token: String?,
    @SerializedName("expiresAt") val expiresAt: Long?,
    @SerializedName("message") val message: String?
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class GenericResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("message") val message: String?
)
