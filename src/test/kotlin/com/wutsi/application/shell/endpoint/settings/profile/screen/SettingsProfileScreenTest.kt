package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsProfileScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/profile"
    }

    @Test
    fun personal() {
        // GIVEN
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        // THEN
        assertEndpointEquals("/screens/settings/profile/profile-personal.json", url)
    }

    @Test
    fun `personal - business account enabled`() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.BUSINESS_ACCOUNT)
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        // THEN
        assertEndpointEquals("/screens/settings/profile/profile-personal-business-enabled.json", url)
    }

    @Test
    fun business() {
        // GIVEN
        setUpBusinessAccount()
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        // THEN
        assertEndpointEquals("/screens/settings/profile/profile-business.json", url)
    }

    @Test
    fun `business - business account enabled`() {
        // WHEN
        setUpBusinessAccount()
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.BUSINESS_ACCOUNT)

        // THEN
        assertEndpointEquals("/screens/settings/profile/profile-business-business-enabled.json", url)
    }

    private fun setUpBusinessAccount() {
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
            website = "https://www.google.ca",
            biography = "Thsi is my bio",
            category = Category(
                id = 1000,
                title = "Marketing"
            ),
            cityId = 2233100,
            whatsapp = "+1237666666666",
            street = "3030 Linton",
            retail = true,
            pictureUrl = "https://www.img.com/1.png"
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())
    }
}
