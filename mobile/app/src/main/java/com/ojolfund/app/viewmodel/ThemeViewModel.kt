package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ojolfund.app.OjolFundApplication
import com.ojolfund.app.data.local.ThemeMode
import com.ojolfund.app.data.local.ThemeStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Menyimpan dan menyediakan pilihan tema (terang/gelap/sistem) untuk seluruh aplikasi. */
class ThemeViewModel(private val store: ThemeStore) : ViewModel() {

    val mode: StateFlow<ThemeMode> = store.modeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    fun setMode(mode: ThemeMode) {
        viewModelScope.launch { store.saveMode(mode) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as OjolFundApplication
                ThemeViewModel(app.container.themeStore)
            }
        }
    }
}
