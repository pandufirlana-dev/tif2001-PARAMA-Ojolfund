package com.ojolfund.app.di

import android.content.Context
import com.ojolfund.app.BuildConfig
import com.ojolfund.app.data.local.DataStoreTokenStore
import com.ojolfund.app.data.local.TokenStore
import com.ojolfund.app.data.remote.AuthApiService
import com.ojolfund.app.data.remote.AuthInterceptor
import com.ojolfund.app.data.repository.AuthRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Dependency injection manual: satu tempat merakit Retrofit, OkHttp, DataStore, dan repository. */
class AppContainer(context: Context) {

    val tokenStore: TokenStore = DataStoreTokenStore(context)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { runBlocking { tokenStore.getToken() } })
        .apply {
            if (BuildConfig.DEBUG) {
                // Level BASIC: hanya method, URL, dan status. Header/body (token, password) tidak dicatat.
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)

    val authRepository: AuthRepository = AuthRepository(authApi, tokenStore)

    private companion object {
        const val TIMEOUT_SECONDS = 15L
    }
}
