package com.ojolfund.app.data.repository

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.ojolfund.app.data.local.TokenStore
import com.ojolfund.app.data.remote.AuthApiService
import com.ojolfund.app.data.remote.dto.ErrorResponse
import com.ojolfund.app.data.remote.dto.LoginRequest
import com.ojolfund.app.data.remote.dto.LoginResponse
import com.ojolfund.app.data.remote.dto.RegisterRequest
import com.ojolfund.app.data.remote.dto.RegisterResponse
import java.io.IOException
import retrofit2.Response

/** Hasil pemanggilan API yang sudah dipetakan: sukses membawa data, gagal membawa pesan siap tampil. */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()

    data class Failure(val message: String, val code: Int? = null) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api: AuthApiService,
    private val tokenStore: TokenStore,
    private val gson: Gson = Gson()
) {

    suspend fun login(email: String, password: String): AuthResult<LoginResponse> = safeCall {
        val response = api.login(LoginRequest(email = email.trim(), password = password))
        val body = response.body()
        if (response.isSuccessful && body != null) {
            tokenStore.saveToken(body.token)
            AuthResult.Success(body)
        } else {
            AuthResult.Failure(errorMessage(response), response.code())
        }
    }

    suspend fun register(name: String, email: String, password: String): AuthResult<RegisterResponse> = safeCall {
        val response = api.register(RegisterRequest(name = name.trim(), email = email.trim(), password = password))
        val body = response.body()
        if (response.isSuccessful && body != null) {
            AuthResult.Success(body)
        } else {
            AuthResult.Failure(errorMessage(response), response.code())
        }
    }

    /** Menghapus token sesi dari penyimpanan lokal. */
    suspend fun logout() = tokenStore.clear()

    private suspend fun <T> safeCall(block: suspend () -> AuthResult<T>): AuthResult<T> = try {
        block()
    } catch (_: IOException) {
        AuthResult.Failure(MSG_NETWORK)
    } catch (_: JsonParseException) {
        AuthResult.Failure(MSG_SERVER)
    }

    /** Pesan dari body galat backend (`{ "message": "..." }`), dengan cadangan berdasarkan kode HTTP. */
    private fun errorMessage(response: Response<*>): String {
        val fromServer = try {
            response.errorBody()?.string()?.let { gson.fromJson(it, ErrorResponse::class.java)?.message }
        } catch (_: JsonParseException) {
            null
        }
        return fromServer?.takeIf { it.isNotBlank() } ?: when (response.code()) {
            400 -> "Permintaan tidak valid"
            401 -> MSG_INVALID_CREDENTIALS
            409 -> "Email sudah terdaftar"
            else -> MSG_SERVER
        }
    }

    companion object {
        const val MSG_INVALID_CREDENTIALS = "Email atau kata sandi salah"
        const val MSG_NETWORK = "Tidak dapat terhubung ke server. Periksa koneksi internet kamu."
        const val MSG_SERVER = "Terjadi kesalahan di server. Coba lagi nanti."
    }
}
