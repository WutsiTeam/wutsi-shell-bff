package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsLinkAccountBankScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/accounts/link/bank"
    }

    @Test
    fun index() = assertEndpointEquals("/shell/screens/settings/accounts/link/bank.json", url)
}
