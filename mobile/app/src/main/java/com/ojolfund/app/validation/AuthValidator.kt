package com.ojolfund.app.validation

/**
 * Validasi form autentikasi di sisi klien, selaras dengan aturan backend
 * (nama wajib, email valid, kata sandi minimal 8 karakter).
 * Setiap fungsi mengembalikan pesan galat, atau `null` jika input valid.
 * Berupa Kotlin murni (tanpa Android) agar mudah diuji dengan unit test.
 */
object AuthValidator {

    const val MIN_PASSWORD_LENGTH = 8
    const val MIN_NAME_LENGTH = 2

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateName(name: String): String? = when {
        name.isBlank() -> "Nama lengkap wajib diisi"
        name.trim().length < MIN_NAME_LENGTH -> "Nama minimal $MIN_NAME_LENGTH karakter"
        else -> null
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email wajib diisi"
        !EMAIL_REGEX.matches(email.trim()) -> "Format email tidak valid"
        else -> null
    }

    /** Saat masuk, kata sandi cukup tidak boleh kosong; kebenarannya diperiksa backend. */
    fun validateLoginPassword(password: String): String? = if (password.isEmpty()) "Kata sandi wajib diisi" else null

    fun validateNewPassword(password: String): String? = when {
        password.isEmpty() -> "Kata sandi wajib diisi"
        password.length < MIN_PASSWORD_LENGTH -> "Kata sandi minimal $MIN_PASSWORD_LENGTH karakter"
        else -> null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Konfirmasi kata sandi wajib diisi"
        confirm != password -> "Konfirmasi kata sandi tidak sama"
        else -> null
    }
}
