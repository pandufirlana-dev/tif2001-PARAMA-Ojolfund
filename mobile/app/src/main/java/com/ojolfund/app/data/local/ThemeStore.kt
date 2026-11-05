package com.ojolfund.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Pilihan tema aplikasi: mengikuti sistem, selalu terang, atau selalu gelap. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Penyimpanan pilihan tema. Antarmuka dipisah agar mudah di-mock pada unit test. */
interface ThemeStore {
    val modeFlow: Flow<ThemeMode>

    suspend fun saveMode(mode: ThemeMode)
}

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Implementasi berbasis Jetpack DataStore Preferences. */
class DataStoreThemeStore(context: Context) : ThemeStore {

    private val dataStore = context.applicationContext.settingsDataStore

    override val modeFlow: Flow<ThemeMode> = dataStore.data.map { prefs ->
        val saved = prefs[KEY_MODE]
        ThemeMode.entries.firstOrNull { it.name == saved } ?: ThemeMode.SYSTEM
    }

    override suspend fun saveMode(mode: ThemeMode) {
        dataStore.edit { it[KEY_MODE] = mode.name }
    }

    private companion object {
        val KEY_MODE = stringPreferencesKey("theme_mode")
    }
}
