package com.wutsi.application.shell.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.ecommerce.shipping.WutsiShippingApi
import com.wutsi.ecommerce.shipping.dto.ListShippingResponse
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import com.wutsi.platform.payment.dto.SearchTransactionResponse
import com.wutsi.platform.payment.dto.TransactionSummary
import com.wutsi.platform.payment.entity.TransactionType
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Ignore

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

    @MockBean
    private lateinit var shippingApi: WutsiShippingApi

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port"

        doReturn(GetBalanceResponse(balance = Balance(amount = 10000.0, currency = "XAF"))).whenever(paymentApi)
            .getBalance(
                any()
            )

        val paymentMethods = listOf(
            createPaymentMethodSummary("A", "11111"),
            createPaymentMethodSummary("B", "22222"),
            createPaymentMethodSummary("C", "33333")
        )
        doReturn(ListPaymentMethodResponse(paymentMethods)).whenever(accountApi).listPaymentMethods(any())

        val accounts = listOf(
            createAccount(USER_ID),
            createAccount(100),
            createAccount(101),
            createAccount(102),
            createAccount(103)
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())

        doReturn(null).whenever(cache).get(any(), eq(Long::class.java))
    }

    @Test
    fun personal() {
        val account = createAccount(business = false)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/shell/screens/home/home-personal.json", url)
    }

    @Test
    fun business() {
        val account = createAccount(business = true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/shell/screens/home/home-business.json", url)
    }

    @Test
    fun accountEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/shell/screens/home/home-account-enabled.json", url)
    }

    @Test
    fun cashinEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.CASHIN)

        assertEndpointEquals("/shell/screens/home/home-cashin-enabled.json", url)
    }

    @Test
    fun cashoutEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.CASHOUT)

        assertEndpointEquals("/shell/screens/home/home-cashout-enabled.json", url)
    }

    @Test
    fun storeEnabled() {
        doReturn(true).whenever(togglesProvider).isStoreEnabled()

        assertEndpointEquals("/shell/screens/home/home-store-enabled.json", url)
    }

    @Test
    fun sendEnabled() {
        doReturn(true).whenever(togglesProvider).isSendEnabled()

        assertEndpointEquals("/shell/screens/home/home-send-enabled.json", url)
    }

    @Test
    fun contactEnabled() {
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        assertEndpointEquals("/shell/screens/home/home-contact-enabled.json", url)
    }

    @Test
    @Ignore
    fun newsEnabled() {
        val user = createAccount(superUser = true)
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.NEWS)
        doReturn(ListShippingResponse()).whenever(shippingApi).listShipping()

        assertEndpointEquals("/shell/screens/home/home-news-enabled.json", url)
    }

    @Test
    fun chatEnabled() {
        val user = createAccount(superUser = true)
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.CHAT)
        doReturn(ListShippingResponse()).whenever(shippingApi).listShipping()

        assertEndpointEquals("/shell/screens/home/home-chat-enabled.json", url)
    }

    @Test
    fun transactionEnabled() {
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.TRANSACTION_HISTORY)

        val txs = listOf(
            createTransaction("A"),
            createTransaction("B"),
            createTransaction("C")
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        val paymentMethods = listOf(
            createPaymentMethodSummary("A", "11111"),
            createPaymentMethodSummary("B", "22222"),
            createPaymentMethodSummary("C", "33333")
        )
        doReturn(ListPaymentMethodResponse(paymentMethods)).whenever(accountApi).listPaymentMethods(any())

        val accounts = listOf(
            createAccount(USER_ID),
            createAccount(USER_ID),
            createAccount(USER_ID)
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())

        assertEndpointEquals("/shell/screens/home/home-transaction-enabled.json", url)
    }

    private fun createTransaction(token: String) = TransactionSummary(
        accountId = USER_ID,
        type = TransactionType.CASHOUT.name,
        status = Status.SUCCESSFUL.name,
        net = 10000.0,
        amount = 10000.0,
        paymentMethodToken = token,
        description = "Sample description",
        created = OffsetDateTime.of(2021, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)
    )

    private fun createPaymentMethodSummary(token: String, maskedNumber: String) = PaymentMethodSummary(
        token = token,
        maskedNumber = maskedNumber,
        type = PaymentMethodType.MOBILE.name,
        provider = PaymentMethodProvider.MTN.name
    )

    private fun createAccount(id: Long) = AccountSummary(
        id = id,
        displayName = "Name $id"
    )
}
