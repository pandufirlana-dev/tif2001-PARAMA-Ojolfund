package com.ojolfund.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Penyimpanan token sesi (JWT). Antarmuka dipisah agar mudah di-mock pada unit test. */
interface TokenStore {
    val tokenFlow: Flow<String?>

    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clear()
}

private val Context.sessionDataStore by preferencesDataStore(name = "session")

/** Implementasi berbasis Jetpack DataStore Preferences (bukan SharedPreferences biasa). */
class DataStoreTokenStore(context: Context) : TokenStore {

    private val dataStore = context.applicationContext.sessionDataStore

    override val tokenFlow: Flow<String?> = dataStore.data.map { it[KEY_TOKEN] }

    override suspend fun getToken(): String? = tokenFlow.first()

    override suspend fun saveToken(token: String) {
        dataStore.edit { it[KEY_TOKEN] = token }
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(KEY_TOKEN) }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("jwt_token")
    }
}
