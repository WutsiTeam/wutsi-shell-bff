package com.wutsi.application.shell.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.ecommerce.shipping.WutsiShippingApi
import com.wutsi.ecommerce.shipping.dto.ListShippingResponse
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import com.wutsi.platform.payment.dto.SearchTransactionResponse
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
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

        doReturn(null).whenever(cache).get(any(), eq(Long::class.java))
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
    fun personal() {
        val account = createAccount(business = false)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-personal.json", url)
    }

    @Test
    fun business() {
        val account = createAccount(business = true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/screens/home/home-business.json", url)
    }

    @Test
    fun accountEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/screens/home/home-account-enabled.json", url)
    }

    @Test
    fun cashinEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.CASHIN)

        assertEndpointEquals("/screens/home/home-cashin-enabled.json", url)
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
    fun contactEnabled() {
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        assertEndpointEquals("/screens/home/home-contact-enabled.json", url)
    }

    @Test
    fun ordersEnabled() {
        user = createAccount(business = true, hasStore = true)
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(togglesProvider).isOrderEnabled()

        assertEndpointEquals("/screens/home/home-orders-enabled.json", url)
    }

    @Test
    fun newsEnabled() {
        val user = createAccount(superUser = true)
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.NEWS)
        doReturn(ListShippingResponse()).whenever(shippingApi).listShipping()

        assertEndpointEquals("/screens/home/home-news-enabled.json", url)
    }

    @Test
    fun noWhatsapp() {
        val account = user.copy(whatsapp = null, business = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-whatsapp.json", url)
        verify(cache).put(eq("$DEVICE_ID-profile-strength"), any())
    }

    @Test
    fun noPicture() {
        val account = user.copy(pictureUrl = null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-picture.json", url)
        verify(cache).put(eq("$DEVICE_ID-profile-strength"), any())
    }

    @Test
    fun noEmail() {
        val account = user.copy(email = null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-email.json", url)
        verify(cache).put(eq("$DEVICE_ID-profile-strength"), any())
    }

    @Test
    fun noCity() {
        val account = user.copy(cityId = null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        assertEndpointEquals("/screens/home/home-no-city.json", url)
        verify(cache).put(eq("$DEVICE_ID-profile-strength"), any())
    }

    @Test
    fun noShipping() {
        val account = user.copy(cityId = 111, business = true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        doReturn(true).whenever(togglesProvider).isShippingEnabled()
        doReturn(ListShippingResponse()).whenever(shippingApi).listShipping()

        assertEndpointEquals("/screens/home/home-no-shipping.json", url)
        verify(cache).put(eq("$DEVICE_ID-profile-strength"), any())
    }

    @Test
    fun alreadyDisplayed() {
        val account =
            user.copy(cityId = null, business = true, hasStore = true, whatsapp = null, email = null, pictureUrl = null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        doReturn(System.currentTimeMillis()).whenever(cache).get(any(), eq(Long::class.java))

        assertEndpointEquals("/screens/home/home-already-displayed.json", url)
        verify(cache, never()).put(eq("$DEVICE_ID-profile-strength"), any())
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
