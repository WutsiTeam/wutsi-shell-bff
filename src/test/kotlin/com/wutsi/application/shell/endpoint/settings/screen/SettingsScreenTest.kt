package com.wutsi.application.shell.endpoint.settings.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings"
    }

    @Test
    fun index() {
        // THEN
        assertEndpointEquals("/screens/settings/settings.json", url)
    }

    @Test
    fun accountEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()
        assertEndpointEquals("/screens/settings/settings-account-enabled.json", url)
    }

    @Test
    fun storeEnabled() {
        setUpBusinessAccount()
        doReturn(true).whenever(togglesProvider).isStoreEnabled()

        assertEndpointEquals("/screens/settings/settings-store-enabled.json", url)
    }

    @Test
    fun orderEnabled() {
        setUpBusinessAccount()
        doReturn(true).whenever(togglesProvider).isOrderEnabled()

        assertEndpointEquals("/screens/settings/settings-order-enabled.json", url)
    }

    private fun setUpBusinessAccount(hasStore: Boolean = true) {
        val account = Account(
            id = ACCOUNT_ID,
            displayName = "Ray Sponsible",
            country = "CM",
            language = "en",
            status = "ACTIVE",
            phone = Phone(
                id = 1,
                number = "+1237666666666",
                country = "CM"
            ),
            business = true,
            hasStore = hasStore
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())
    }
}
