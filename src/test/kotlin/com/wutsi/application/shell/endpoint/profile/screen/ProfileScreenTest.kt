package com.wutsi.application.shell.endpoint.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.ecommerce.cart.WutsiCartApi
import com.wutsi.ecommerce.cart.dto.Cart
import com.wutsi.ecommerce.cart.dto.GetCartResponse
import com.wutsi.ecommerce.cart.dto.Product
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactResponse
import com.wutsi.platform.tenant.entity.ToggleName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ProfileScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var contactApi: WutsiContactApi

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @MockBean
    private lateinit var cartApi: WutsiCartApi

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())
    }

    @Test
    fun personal() {
        // GIVEN
        val account = createAccount(555, false, null)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/personal.json", response.body)
    }

    @Test
    fun storeEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.BUSINESS_ACCOUNT)
        doReturn(true).whenever(togglesProvider).isStoreEnabled()

        val account = createAccount(555, true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555&tab=store"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/store-enabled.json", response.body)
    }

    @Test
    fun cartEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.BUSINESS_ACCOUNT)
        doReturn(true).whenever(togglesProvider).isStoreEnabled()
        doReturn(true).whenever(togglesProvider).isCartEnabled()

        val cart = Cart(
            products = listOf(Product(), Product())
        )
        doReturn(GetCartResponse(cart)).whenever(cartApi).getCart(any())

        val account = createAccount(555, true, hasStore = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/cart-enabled.json", response.body)
    }

    @Test
    fun business() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.BUSINESS_ACCOUNT)

        val account = createAccount(555, true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/business.json", response.body)
    }

    @Test
    fun contactEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        val account = createAccount(555, true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/contact-enabled.json", response.body)
    }

    @Test
    fun chatEnabled() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isToggleEnabled(ToggleName.CHAT)

        val account = createAccount(555, true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/chat-enabled.json", response.body)
    }

    @Test
    fun accountSuspended() {
        // GIVEN
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        val account = createAccount(555, business = true, status = AccountStatus.SUSPENDED)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/suspended.json", response.body)
    }

    private fun createAccount(
        id: Long,
        business: Boolean,
        pictureUrl: String? = "https://img.com/1.png",
        hasStore: Boolean = false,
        status: AccountStatus = AccountStatus.ACTIVE
    ) = Account(
        id = id,
        displayName = "Ray Sponsible",
        country = "CM",
        language = "en",
        phone = Phone(
            id = 1,
            number = "+1237666666666",
            country = "CM"
        ),
        pictureUrl = pictureUrl,
        business = business,
        biography = "This is my bio",
        category = Category(
            id = 1000,
            title = "Marketing"
        ),
        website = "https://my.business.com/12432",
        whatsapp = "+23500000000",
        hasStore = hasStore,
        status = status.name
    )
}
