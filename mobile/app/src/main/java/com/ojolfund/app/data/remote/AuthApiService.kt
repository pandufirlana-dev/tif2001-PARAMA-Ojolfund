package com.ojolfund.app.data.remote

import com.ojolfund.app.data.remote.dto.LoginRequest
import com.ojolfund.app.data.remote.dto.LoginResponse
import com.ojolfund.app.data.remote.dto.MeResponse
import com.ojolfund.app.data.remote.dto.RegisterRequest
import com.ojolfund.app.data.remote.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Kontrak endpoint autentikasi (lihat docs/api_specifications.md).
 * Base URL: [com.ojolfund.app.BuildConfig.BASE_URL] (berakhir dengan `/`).
 */
interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    /** Butuh header `Authorization: Bearer <token>` (disematkan otomatis oleh [AuthInterceptor]). */
    @GET("auth/me")
    suspend fun me(): Response<MeResponse>
}
