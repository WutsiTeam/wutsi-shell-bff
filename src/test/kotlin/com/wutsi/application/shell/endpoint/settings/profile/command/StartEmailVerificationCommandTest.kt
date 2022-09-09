package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.security.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class StartEmailVerificationCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun index() {
        // GIVEN
        val data = CreateOTPResponse(
            token = "xxx"
        )
        doReturn(data).whenever(securityApi).createOpt(any())

        // WHEN
        val request = UpdateAccountAttributeRequest(
            value = "ray.spnsible@gmail.com"
        )
        val url = "http://localhost:$port/commands/start-email-verification"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/profile/email/verification", action.url)
        assertEquals(
            mapOf(
                "email" to request.value,
                "token" to data.token
            ),
            action.parameters
        )
        assertEquals(true, action.replacement)
    }
}
