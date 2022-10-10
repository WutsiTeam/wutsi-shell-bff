package com.wutsi.application.shell.endpoint.settings.about.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SwitchEnvironmntCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var logout: LogoutCommand

    @Test
    fun index() {
        val url = "http://localhost:$port/commands/switch-environment?environment=prod"
        val response = rest.postForEntity(url, null, Action::class.java)

        verify(logout).execute()

        assertEquals(200, response.statusCodeValue)
        assertEquals("prod", response.headers["x-environment"]?.get(0))

        val action = response.body!!
        assertEquals("route:/", action.url)
        assertEquals(ActionType.Route, action.type)
    }
}
