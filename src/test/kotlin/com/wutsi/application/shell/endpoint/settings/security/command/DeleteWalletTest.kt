package com.wutsi.application.shell.endpoint.settings.security.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DeleteWalletTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var logout: LogoutCommand

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/delete-wallet"
    }

    @Test
    fun invoke() {
        val response = rest.postForEntity(url, null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(accountApi).suspendAccount(ACCOUNT_ID)
        verify(logout).execute()

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("https://wutsi-login-bff-test.herokuapp.com/onboard", action.url)
    }
}
