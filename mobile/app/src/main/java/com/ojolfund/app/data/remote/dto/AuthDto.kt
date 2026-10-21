package com.ojolfund.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Body `POST /auth/login`. */
data class LoginRequest(val email: String, val password: String)

/** Body `POST /auth/register`. */
data class RegisterRequest(val name: String, val email: String, val password: String)

/** Data pengguna dari backend. Field bersifat nullable karena `GET /auth/me` hanya mengirim `id` dan `role`. */
data class UserDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

/** Response 200 `POST /auth/login`. */
data class LoginResponse(val message: String, val token: String, val user: UserDto)

/** Response 201 `POST /auth/register`. */
data class RegisterResponse(val message: String, val user: UserDto)

/** Response 200 `GET /auth/me`. */
data class MeResponse(val message: String, val user: UserDto)

/** Response galat (400, 401, 409, 500): `{ "message": "..." }`. */
data class ErrorResponse(val message: String? = null)
