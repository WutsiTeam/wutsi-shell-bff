package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.endpoint.settings.profile.dto.VerifyOtpRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.security.dto.VerifyOTPRequest
import com.wutsi.platform.security.util.ErrorURN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class VerifyEmailCommandTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val email: String = "ray.sponsible@gmail.com"
    private val token: String = "xxxx"

    @Test
    fun index() {
        // WHEN
        val request = VerifyOtpRequest(
            code = "123456"
        )
        val url = "http://localhost:$port/commands/verify-email?email=$email&token=$token"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(securityApi).verifyOtp(token, VerifyOTPRequest(code = request.code))
        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "email", UpdateAccountAttributeRequest(email))

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
    }

    @Test
    fun validationError() {
        // GIVEN
        val ex = createFeignException(errorCode = ErrorURN.MFA_VERIFICATION_FAILED.urn)
        doThrow(ex).whenever(securityApi).verifyOtp(any(), any())

        // WHEN
        val request = VerifyOtpRequest(
            code = "123456"
        )
        val url = "http://localhost:$port/commands/verify-email?email=$email&token=$token"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(accountApi, never()).updateAccountAttribute(any(), any(), any())

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.otp-mismatch"), action.prompt?.attributes?.get("message"))
    }
}
