package com.wutsi.application.shell.endpoint.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsAccountScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

    @MockBean
    private lateinit var toggleProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/account"

        val balance = Balance(
            currency = "XAF",
            amount = 150000.0
        )
        doReturn(GetBalanceResponse(balance)).whenever(paymentApi).getBalance(any())

        val m1 = PaymentMethodSummary(
            token = "123",
            type = PaymentMethodType.MOBILE.name,
            provider = PaymentMethodProvider.MTN.name,
            maskedNumber = "...1111"
        )
        val m2 = PaymentMethodSummary(
            token = "456",
            type = PaymentMethodType.MOBILE.name,
            provider = PaymentMethodProvider.ORANGE.name,
            maskedNumber = "...1122"
        )
        val m3 = PaymentMethodSummary(
            token = "456",
            type = PaymentMethodType.BANK.name,
            provider = PaymentMethodProvider.WAF.name,
            maskedNumber = "...3456"
        )
        val m4 = PaymentMethodSummary(
            token = "054909",
            type = PaymentMethodType.CREDIT_CARD.name,
            provider = PaymentMethodProvider.MASTERCARD.name,
            maskedNumber = "...4444"
        )
        doReturn(ListPaymentMethodResponse(listOf(m1, m2, m3, m4))).whenever(accountApi).listPaymentMethods(any())
    }

    @Test
    fun `personnal account`() {
        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/account-personal.json", url)
    }

    @Test
    fun `transaction history enabled`() {
        user = createAccount(true, Category(id = 100, title = "Foo"))
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(toggleProvider).isToggleEnabled(ToggleName.TRANSACTION_HISTORY)

        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/account-history-enabled.json", url)
    }

    @Test
    fun `business account`() {
        // GIVEN
        user = createAccount(true, Category(id = 100, title = "Foo"))
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/account-business.json", url)
    }

    @Test
    fun `cashout enabled`() {
        // GIVEN
        user = createAccount(true, Category(id = 100, title = "Foo"))
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())
        doReturn(true).whenever(toggleProvider).isToggleEnabled(ToggleName.CASHOUT)

        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/account-cashout-enabled.json", url)
    }

    @Test
    fun `cashin enabled`() {
        // GIVEN
        user = createAccount(true, Category(id = 100, title = "Foo"))
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())
        doReturn(true).whenever(toggleProvider).isToggleEnabled(ToggleName.CASHOUT)

        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/account-cashin-enabled.json", url)
    }
}
