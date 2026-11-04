package com.ojolfund.app.data.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private val url = "http://localhost/api/v1/auth/me"

    @Test
    fun addsBearerHeaderWhenTokenAvailable() {
        val sent = interceptWith(token = "abc.def.ghi", request = Request.Builder().url(url).build())

        assertEquals("Bearer abc.def.ghi", sent.header("Authorization"))
    }

    @Test
    fun skipsHeaderWhenTokenNull() {
        val sent = interceptWith(token = null, request = Request.Builder().url(url).build())

        assertNull(sent.header("Authorization"))
    }

    @Test
    fun skipsHeaderWhenTokenBlank() {
        val sent = interceptWith(token = "  ", request = Request.Builder().url(url).build())

        assertNull(sent.header("Authorization"))
    }

    @Test
    fun keepsExistingAuthorizationHeader() {
        val request = Request.Builder().url(url).header("Authorization", "Bearer manual").build()

        val sent = interceptWith(token = "other", request = request)

        assertEquals("Bearer manual", sent.header("Authorization"))
    }

    private fun interceptWith(token: String?, request: Request): Request {
        val captured = slot<Request>()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(capture(captured)) } answers {
            Response.Builder()
                .request(captured.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }

        AuthInterceptor { token }.intercept(chain)
        return captured.captured
    }
}
