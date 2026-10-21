package com.ojolfund.app.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Menyematkan header `Authorization: Bearer <token>` pada setiap request
 * jika token tersedia dan request belum membawa header Authorization sendiri.
 *
 * [tokenProvider] dipisah sebagai lambda agar mudah diuji tanpa Android/DataStore.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider()

        if (token.isNullOrBlank() || original.header(HEADER_AUTHORIZATION) != null) {
            return chain.proceed(original)
        }

        val authorized = original.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
            .build()
        return chain.proceed(authorized)
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}
