package com.wutsi.application.shell.endpoint.settings.security.command

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DeleteWalletTest : AbstractEndpointTest() {
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
        val action = Action()
        doReturn(action).whenever(logout).index()

        val response = rest.postForEntity(url, null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        verify(accountApi).suspendAccount(ACCOUNT_ID)
        verify(logout).index()
    }
}

