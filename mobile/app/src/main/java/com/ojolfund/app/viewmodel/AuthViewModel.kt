package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ojolfund.app.OjolFundApplication
import com.ojolfund.app.data.repository.AuthRepository
import com.ojolfund.app.data.repository.AuthResult
import com.ojolfund.app.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Status proses autentikasi; [LoggedIn] dan [Registered] adalah sinyal sekali pakai untuk navigasi. */
sealed class AuthStatus {
    data object Idle : AuthStatus()

    data object Loading : AuthStatus()

    data object LoggedIn : AuthStatus()

    data object Registered : AuthStatus()

    data class Error(val message: String) : AuthStatus()
}

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val status: AuthStatus = AuthStatus.Idle,
    val notice: String? = null
) {
    val isLoading: Boolean get() = status is AuthStatus.Loading
    val errorMessage: String? get() = (status as? AuthStatus.Error)?.message
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update {
        it.copy(name = value, nameError = null, status = it.clearedStatus())
    }

    fun onEmailChange(value: String) = _state.update {
        it.copy(email = value, emailError = null, status = it.clearedStatus())
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(password = value, passwordError = null, status = it.clearedStatus())
    }

    fun onConfirmPasswordChange(value: String) = _state.update {
        it.copy(confirmPassword = value, confirmError = null, status = it.clearedStatus())
    }

    fun login() {
        val current = _state.value
        if (current.isLoading) return

        val emailError = AuthValidator.validateEmail(current.email)
        val passwordError = AuthValidator.validateLoginPassword(current.password)
        if (emailError != null || passwordError != null) {
            _state.update { it.copy(emailError = emailError, passwordError = passwordError, notice = null) }
            return
        }

        _state.update { it.copy(status = AuthStatus.Loading, notice = null) }
        viewModelScope.launch {
            when (val result = repository.login(current.email, current.password)) {
                is AuthResult.Success -> _state.update { it.copy(password = "", status = AuthStatus.LoggedIn) }
                is AuthResult.Failure -> _state.update { it.copy(status = AuthStatus.Error(result.message)) }
            }
        }
    }

    fun register() {
        val current = _state.value
        if (current.isLoading) return

        val nameError = AuthValidator.validateName(current.name)
        val emailError = AuthValidator.validateEmail(current.email)
        val passwordError = AuthValidator.validateNewPassword(current.password)
        val confirmError = AuthValidator.validateConfirmPassword(current.password, current.confirmPassword)
        if (listOf(nameError, emailError, passwordError, confirmError).any { it != null }) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmError = confirmError,
                    notice = null
                )
            }
            return
        }

        _state.update { it.copy(status = AuthStatus.Loading, notice = null) }
        viewModelScope.launch {
            val result = repository.register(current.name, current.email, current.password)
            when (result) {
                is AuthResult.Success -> _state.update {
                    // Email dipertahankan agar layar Masuk langsung terisi.
                    AuthUiState(
                        email = current.email.trim(),
                        status = AuthStatus.Registered,
                        notice = "Akun berhasil dibuat. Silakan masuk."
                    )
                }
                is AuthResult.Failure -> _state.update {
                    // 409 = email sudah terdaftar: tampilkan sebagai galat pada field email.
                    if (result.code == HTTP_CONFLICT) {
                        it.copy(status = AuthStatus.Idle, emailError = result.message)
                    } else {
                        it.copy(status = AuthStatus.Error(result.message))
                    }
                }
            }
        }
    }

    /** Menghapus token sesi lalu mengosongkan form. */
    fun logout() {
        viewModelScope.launch { repository.logout() }
        _state.value = AuthUiState()
    }

    /** Mengosongkan form dan galat (dipanggil saat berpindah antar layar autentikasi). */
    fun resetForm() {
        _state.value = AuthUiState()
    }

    /** Dipanggil UI setelah sinyal navigasi ([AuthStatus.LoggedIn]/[AuthStatus.Registered]) ditangani. */
    fun consumeStatus() = _state.update { it.copy(status = AuthStatus.Idle) }

    private fun AuthUiState.clearedStatus(): AuthStatus = if (status is AuthStatus.Error) AuthStatus.Idle else status

    companion object {
        private const val HTTP_CONFLICT = 409

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as OjolFundApplication
                AuthViewModel(app.container.authRepository)
            }
        }
    }
}
