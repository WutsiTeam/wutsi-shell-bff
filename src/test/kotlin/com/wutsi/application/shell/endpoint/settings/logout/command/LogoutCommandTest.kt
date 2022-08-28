package com.wutsi.application.shell.endpoint.settings.logout.command

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.security.spring.RequestTokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.LogoutRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class LogoutCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var securityApi: WutsiSecurityApi

    @MockBean
    private lateinit var tokenProvider: RequestTokenProvider

    @Test
    fun logout() {
        // GIVEN
        val token = "xxx"
        doReturn(token).whenever(tokenProvider).getToken()

        // WHEN
        val url = "http://localhost:$port/commands/logout"
        val response = rest.postForEntity(url, null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(securityApi).logout(LogoutRequest(token))

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/~", action.url)
        assertEquals(true, action.replacement)
    }
}
