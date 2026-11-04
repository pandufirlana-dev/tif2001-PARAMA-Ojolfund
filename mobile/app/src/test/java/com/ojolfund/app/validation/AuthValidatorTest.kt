package com.ojolfund.app.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun emailBlankIsRequired() {
        assertEquals("Email wajib diisi", AuthValidator.validateEmail("   "))
    }

    @Test
    fun emailWithoutAtSignIsInvalid() {
        assertEquals("Format email tidak valid", AuthValidator.validateEmail("rafimail.com"))
    }

    @Test
    fun emailWithoutDomainSuffixIsInvalid() {
        assertEquals("Format email tidak valid", AuthValidator.validateEmail("rafi@mail"))
    }

    @Test
    fun validEmailPassesEvenWithSurroundingSpaces() {
        assertNull(AuthValidator.validateEmail("  rafi.alfa@mail.co.id "))
    }

    @Test
    fun nameBlankIsRequired() {
        assertEquals("Nama lengkap wajib diisi", AuthValidator.validateName(""))
    }

    @Test
    fun nameWithOneCharIsTooShort() {
        assertEquals("Nama minimal 2 karakter", AuthValidator.validateName(" R "))
    }

    @Test
    fun validNamePasses() {
        assertNull(AuthValidator.validateName("Rafi"))
    }

    @Test
    fun loginPasswordEmptyIsRequired() {
        assertEquals("Kata sandi wajib diisi", AuthValidator.validateLoginPassword(""))
    }

    @Test
    fun loginPasswordShortIsAllowedBecauseBackendChecksIt() {
        assertNull(AuthValidator.validateLoginPassword("abc"))
    }

    @Test
    fun newPasswordSevenCharsIsRejected() {
        assertEquals("Kata sandi minimal 8 karakter", AuthValidator.validateNewPassword("1234567"))
    }

    @Test
    fun newPasswordEightCharsPasses() {
        assertNull(AuthValidator.validateNewPassword("12345678"))
    }

    @Test
    fun confirmPasswordMismatchIsRejected() {
        val error = AuthValidator.validateConfirmPassword("rahasia123", "rahasia1")

        assertEquals("Konfirmasi kata sandi tidak sama", error)
    }

    @Test
    fun confirmPasswordMatchPasses() {
        assertNull(AuthValidator.validateConfirmPassword("rahasia123", "rahasia123"))
    }
}
