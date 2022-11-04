package com.wutsi.application.shell.endpoint.settings.account.screen

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsLinkAccountScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/accounts/link"
    }

    @Test
    fun mobileMoney() {
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.ACCOUNT_MOBILE_MONEY)

        assertEndpointEquals("/shell/screens/settings/accounts/link/link-mobile-money.json", url)
    }

    @Test
    fun bank() {
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.ACCOUNT_BANK)

        assertEndpointEquals("/shell/screens/settings/accounts/link/link-with-bank-enabled.json", url)
    }

    @Test
    fun creditCard() {
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.ACCOUNT_CREDIT_CARD)

        assertEndpointEquals("/shell/screens/settings/accounts/link/link-with-credit-card-enabled.json", url)
    }
}
