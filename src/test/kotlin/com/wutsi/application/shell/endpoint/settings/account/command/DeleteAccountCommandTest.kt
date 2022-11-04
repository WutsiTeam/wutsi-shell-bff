package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DeleteAccountCommandTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    private val token = "xxx"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/delete-account?token=$token"
    }

    @Test
    fun index() {
        // WHEN
        val response = rest.postForEntity(url, null, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(accountApi).deactivatePaymentMethod(ACCOUNT_ID, token)
    }
}
