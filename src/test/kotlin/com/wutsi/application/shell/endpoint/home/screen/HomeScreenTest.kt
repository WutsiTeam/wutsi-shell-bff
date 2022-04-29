package com.wutsi.application.shell.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import com.wutsi.platform.payment.dto.SearchTransactionResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

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
            createPaymentMethodSummary("C", "33333"),
        )
        doReturn(ListPaymentMethodResponse(paymentMethods)).whenever(accountApi).listPaymentMethods(any())

        val accounts = listOf(
            createAccount(USER_ID),
            createAccount(100),
            createAccount(101),
            createAccount(102),
            createAccount(103),
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())
    }

    @Test
    fun noAccount() {
        doReturn(GetBalanceResponse(balance = Balance(amount = 0.0, currency = "XAF"))).whenever(paymentApi)
            .getBalance(
                any()
            )

        doReturn(SearchTransactionResponse()).whenever(paymentApi).searchTransaction(any())
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        assertEndpointEquals("/screens/home/home-no-account.json", url)
    }

    @Test
    fun home() {
        assertEndpointEquals("/screens/home/home.json", url)
    }

    @Test
    fun accountEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/screens/home/home-account-enabled.json", url)
    }
    
    @Test
    fun storeEnabled() {
        doReturn(true).whenever(togglesProvider).isStoreEnabled()

        assertEndpointEquals("/screens/home/home-store-enabled.json", url)
    }

    @Test
    fun sendEnabled() {
        doReturn(true).whenever(togglesProvider).isSendEnabled()

        assertEndpointEquals("/screens/home/home-send-enabled.json", url)
    }

    @Test
    fun noWhatsapp() {
        val account = Account(
            id = ACCOUNT_ID,
            displayName = "Ray Sponsible",
            business = true,
            hasStore = true,
            pictureUrl = "https://www.google.ca",
            whatsapp = null
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-whatsapp.json", url)
    }

    @Test
    fun noPicture() {
        val account = Account(
            id = ACCOUNT_ID,
            displayName = "Ray Sponsible",
            business = false,
            hasStore = true,
            pictureUrl = null,
            whatsapp = null
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-picture.json", url)
    }

    private fun createPaymentMethodSummary(token: String, maskedNumber: String) = PaymentMethodSummary(
        token = token,
        maskedNumber = maskedNumber
    )

    private fun createAccount(id: Long) = AccountSummary(
        id = id,
        displayName = "Name $id"
    )
}
