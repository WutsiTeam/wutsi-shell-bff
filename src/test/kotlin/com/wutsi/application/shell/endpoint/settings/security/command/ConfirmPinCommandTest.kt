package com.wutsi.application.shell.endpoint.settings.security.command

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.security.dto.ChangePinRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.SavePasswordRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ConfirmPinCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/confirm-pin?pin=123456"
    }

    @Test
    fun run() {
        // WHEN
        val request = ChangePinRequest(
            pin = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<SavePasswordRequest>()
        verify(accountApi).savePassword(eq(ACCOUNT_ID), req.capture())
        kotlin.test.assertEquals(request.pin, req.firstValue.password)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/security/pin/success", action.url)
    }

    @Test
    fun passwordMismatch() {
        // WHEN
        val request = ChangePinRequest(
            pin = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<SavePasswordRequest>()
        verify(accountApi).savePassword(eq(ACCOUNT_ID), req.capture())
        kotlin.test.assertEquals(request.pin, req.firstValue.password)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/security/pin/success", action.url)
    }
}
