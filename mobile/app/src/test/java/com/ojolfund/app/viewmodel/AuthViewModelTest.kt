package com.ojolfund.app.viewmodel

import com.ojolfund.app.data.local.TokenStore
import com.ojolfund.app.data.remote.AuthApiService
import com.ojolfund.app.data.remote.dto.LoginRequest
import com.ojolfund.app.data.remote.dto.LoginResponse
import com.ojolfund.app.data.remote.dto.RegisterRequest
import com.ojolfund.app.data.remote.dto.RegisterResponse
import com.ojolfund.app.data.remote.dto.UserDto
import com.ojolfund.app.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Unit test JVM lokal (tanpa emulator) untuk [AuthViewModel].
 * API dan penyimpanan token di-mock dengan MockK; coroutine dikendalikan lewat [StandardTestDispatcher].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var api: AuthApiService
    private lateinit var tokenStore: TokenStore
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        tokenStore = mockk()
        coJustRun { tokenStore.saveToken(any()) }
        coJustRun { tokenStore.clear() }
        viewModel = AuthViewModel(AuthRepository(api, tokenStore))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Skenario wajib 1 & 2: validasi input

    @Test
    fun loginWithInvalidEmailFormatShowsEmailError() = runTest(testDispatcher) {
        viewModel.onEmailChange("rafi-tanpa-at")
        viewModel.onPasswordChange("rahasia123")

        viewModel.login()
        advanceUntilIdle()

        assertEquals("Format email tidak valid", viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
        assertEquals(AuthStatus.Idle, viewModel.state.value.status)
        coVerify(exactly = 0) { api.login(any()) }
    }

    @Test
    fun registerWithPasswordUnderEightCharsIsRejected() = runTest(testDispatcher) {
        viewModel.onNameChange("Rafi Alfa")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("abc1234")
        viewModel.onConfirmPasswordChange("abc1234")

        viewModel.register()
        advanceUntilIdle()

        assertEquals("Kata sandi minimal 8 karakter", viewModel.state.value.passwordError)
        assertEquals(AuthStatus.Idle, viewModel.state.value.status)
        coVerify(exactly = 0) { api.register(any()) }
    }

    // endregion

    // region Skenario wajib 3: login sukses (Loading -> LoggedIn + token tersimpan)

    @Test
    fun loginSuccessMovesFromLoadingToLoggedInAndStoresToken() = runTest(testDispatcher) {
        coEvery { api.login(any()) } returns Response.success(loginResponse(token = "jwt-token-123"))
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")

        viewModel.login()
        assertEquals(AuthStatus.Loading, viewModel.state.value.status)
        assertEquals(true, viewModel.state.value.isLoading)

        advanceUntilIdle()

        assertEquals(AuthStatus.LoggedIn, viewModel.state.value.status)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals("", viewModel.state.value.password)
        coVerify(exactly = 1) { api.login(LoginRequest("rafi@mail.com", "rahasia123")) }
        coVerify(exactly = 1) { tokenStore.saveToken("jwt-token-123") }
    }

    // endregion

    // region Skenario wajib 4: HTTP 401

    @Test
    fun loginWith401ShowsInvalidCredentialsError() = runTest(testDispatcher) {
        coEvery { api.login(any()) } returns errorResponse(401, """{"message":"Email atau kata sandi salah"}""")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("salahsemua")

        viewModel.login()
        advanceUntilIdle()

        assertEquals(AuthStatus.Error("Email atau kata sandi salah"), viewModel.state.value.status)
        assertEquals("Email atau kata sandi salah", viewModel.state.value.errorMessage)
        coVerify(exactly = 0) { tokenStore.saveToken(any()) }
    }

    // endregion

    // region Skenario tambahan

    @Test
    fun loginWithBlankPasswordShowsPasswordError() = runTest(testDispatcher) {
        viewModel.onEmailChange("rafi@mail.com")

        viewModel.login()
        advanceUntilIdle()

        assertEquals("Kata sandi wajib diisi", viewModel.state.value.passwordError)
        coVerify(exactly = 0) { api.login(any()) }
    }

    @Test
    fun loginWith401WithoutBodyFallsBackToDefaultMessage() = runTest(testDispatcher) {
        coEvery { api.login(any()) } returns errorResponse(401, "")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("salahsemua")

        viewModel.login()
        advanceUntilIdle()

        assertEquals(AuthStatus.Error(AuthRepository.MSG_INVALID_CREDENTIALS), viewModel.state.value.status)
    }

    @Test
    fun loginWithNetworkFailureShowsNetworkMessage() = runTest(testDispatcher) {
        coEvery { api.login(any()) } throws IOException("timeout")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")

        viewModel.login()
        advanceUntilIdle()

        assertEquals(AuthStatus.Error(AuthRepository.MSG_NETWORK), viewModel.state.value.status)
    }

    @Test
    fun typingAfterErrorClearsErrorStatus() = runTest(testDispatcher) {
        coEvery { api.login(any()) } returns errorResponse(401, """{"message":"Email atau kata sandi salah"}""")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("salahsemua")
        viewModel.login()
        advanceUntilIdle()

        viewModel.onPasswordChange("coba-lagi-123")

        assertEquals(AuthStatus.Idle, viewModel.state.value.status)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun consumeStatusResetsLoggedInToIdle() = runTest(testDispatcher) {
        coEvery { api.login(any()) } returns Response.success(loginResponse())
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")
        viewModel.login()
        advanceUntilIdle()

        viewModel.consumeStatus()

        assertEquals(AuthStatus.Idle, viewModel.state.value.status)
    }

    @Test
    fun registerWithMismatchedConfirmationShowsConfirmError() = runTest(testDispatcher) {
        viewModel.onNameChange("Rafi Alfa")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")
        viewModel.onConfirmPasswordChange("rahasia124")

        viewModel.register()
        advanceUntilIdle()

        assertEquals("Konfirmasi kata sandi tidak sama", viewModel.state.value.confirmError)
        coVerify(exactly = 0) { api.register(any()) }
    }

    @Test
    fun registerSuccessEmitsRegisteredAndKeepsEmail() = runTest(testDispatcher) {
        coEvery { api.register(any()) } returns Response.success(
            RegisterResponse("Registrasi berhasil", UserDto(id = "u1", name = "Rafi Alfa", email = "rafi@mail.com"))
        )
        viewModel.onNameChange("Rafi Alfa")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")
        viewModel.onConfirmPasswordChange("rahasia123")

        viewModel.register()
        advanceUntilIdle()

        assertEquals(AuthStatus.Registered, viewModel.state.value.status)
        assertEquals("rafi@mail.com", viewModel.state.value.email)
        assertEquals("", viewModel.state.value.password)
        coVerify(exactly = 1) { api.register(RegisterRequest("Rafi Alfa", "rafi@mail.com", "rahasia123")) }
    }

    @Test
    fun registerWith409ShowsEmailAlreadyRegisteredOnEmailField() = runTest(testDispatcher) {
        coEvery { api.register(any()) } returns errorResponse(409, """{"message":"Email sudah terdaftar"}""")
        viewModel.onNameChange("Rafi Alfa")
        viewModel.onEmailChange("rafi@mail.com")
        viewModel.onPasswordChange("rahasia123")
        viewModel.onConfirmPasswordChange("rahasia123")

        viewModel.register()
        advanceUntilIdle()

        assertEquals("Email sudah terdaftar", viewModel.state.value.emailError)
        assertEquals(AuthStatus.Idle, viewModel.state.value.status)
    }

    @Test
    fun logoutClearsTokenAndResetsForm() = runTest(testDispatcher) {
        viewModel.onEmailChange("rafi@mail.com")

        viewModel.logout()
        advanceUntilIdle()

        assertEquals(AuthUiState(), viewModel.state.value)
        coVerify(exactly = 1) { tokenStore.clear() }
    }

    // endregion

    private fun loginResponse(token: String = "jwt-token") = LoginResponse(
        message = "Login berhasil",
        token = token,
        user = UserDto(id = "u1", name = "Rafi Alfa", email = "rafi@mail.com", role = "driver")
    )

    private fun <T> errorResponse(code: Int, json: String): Response<T> =
        Response.error(code, json.toResponseBody("application/json".toMediaType()))
}
