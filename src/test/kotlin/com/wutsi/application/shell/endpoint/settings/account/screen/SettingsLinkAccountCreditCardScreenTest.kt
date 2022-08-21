package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsLinkAccountCreditCardScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/accounts/link/credit-card"
    }

    @Test
    fun index() = assertEndpointEquals("/screens/settings/accounts/link/credit-card.json", url)
}
