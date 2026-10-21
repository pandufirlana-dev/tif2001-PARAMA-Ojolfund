package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OtpMethod { WHATSAPP, SMS }

data class OtpAuthUiState(
    val phone: String = "81287654321", // digit saja, tanpa +62 / 0 di depan
    val phoneError: String? = null,
    val method: OtpMethod = OtpMethod.WHATSAPP,
    val otp: String = "",
    val otpError: String? = null,
    val secondsLeft: Int = 43,
    val isLoading: Boolean = false
)

class OtpAuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(OtpAuthUiState())
    val state: StateFlow<OtpAuthUiState> = _state.asStateFlow()

    // ---------- Nomor HP ----------
    fun onPhoneAppend(digit: Char) {
        _state.update {
            if (it.phone.length >= 12 || !digit.isDigit()) {
                it
            } else {
                it.copy(phone = it.phone + digit, phoneError = null)
            }
        }
    }

    fun onPhoneChange(raw: String) {
        val digits = raw.filter { it.isDigit() }.removePrefix("0").take(12)
        _state.update { it.copy(phone = digits, phoneError = null) }
    }

    fun onPhoneBackspace() {
        _state.update { it.copy(phone = it.phone.dropLast(1), phoneError = null) }
    }

    fun setMethod(method: OtpMethod) {
        _state.update { it.copy(method = method) }
    }

    fun validatePhone(): String? = when {
        _state.value.phone.length < 9 -> "Nomor handphone minimal 9 digit"
        !_state.value.phone.startsWith("8") -> "Nomor harus diawali angka 8 (contoh: 812...)"
        else -> null
    }

    /** Validasi nomor, simulasi kirim OTP, lalu panggil [onSuccess]. */
    fun sendOtp(onSuccess: () -> Unit) {
        val error = validatePhone()
        if (error != null) {
            _state.update { it.copy(phoneError = error) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, otp = "", otpError = null, secondsLeft = 43) }
            delay(600)
            _state.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }

    // ---------- OTP ----------
    fun onOtpDigit(digit: Char) {
        _state.update {
            if (it.otp.length >= 6) it else it.copy(otp = it.otp + digit, otpError = null)
        }
    }

    fun onOtpBackspace() {
        _state.update { it.copy(otp = it.otp.dropLast(1), otpError = null) }
    }

    fun tick() {
        _state.update { if (it.secondsLeft > 0) it.copy(secondsLeft = it.secondsLeft - 1) else it }
    }

    fun resend() {
        _state.update { it.copy(secondsLeft = 60, otp = "", otpError = null) }
    }

    /** Untuk demo: OTP valid jika 6 digit. */
    fun verifyOtp(onSuccess: () -> Unit) {
        if (_state.value.otp.length < 6) {
            _state.update { it.copy(otpError = "Kode OTP harus 6 digit") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(600)
            _state.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
