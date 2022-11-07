package com.wutsi.application.store.endpoint.order.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.store.endpoint.AbstractEndpointTest
import com.wutsi.ecommerce.catalog.dto.PictureSummary
import com.wutsi.ecommerce.catalog.dto.ProductSummary
import com.wutsi.ecommerce.catalog.dto.SearchProductResponse
import com.wutsi.ecommerce.order.WutsiOrderApi
import com.wutsi.ecommerce.order.dto.OrderSummary
import com.wutsi.ecommerce.order.dto.SearchOrderResponse
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.SearchAccountResponse
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class OrdersScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var orderApi: WutsiOrderApi

    private val orders = listOf(
        OrderSummary(
            id = "111",
            merchantId = 55L,
            accountId = 111L,
            totalPrice = 25000.0,
            subTotalPrice = 30000.0,
            savingsAmount = 5000.0,
            currency = "XAF",
            status = OrderStatus.DONE.name,
            reservationId = 777L,
            created = OffsetDateTime.of(2020, 5, 5, 1, 1, 0, 0, ZoneOffset.UTC),
            itemCount = 1,
            productIds = listOf(100L)
        ),
        OrderSummary(
            id = "222",
            merchantId = 55L,
            accountId = 222L,
            totalPrice = 50000.0,
            subTotalPrice = 30000.0,
            savingsAmount = 5000.0,
            currency = "XAF",
            status = OrderStatus.OPENED.name,
            reservationId = 777L,
            created = OffsetDateTime.of(2020, 6, 5, 1, 1, 0, 0, ZoneOffset.UTC),
            itemCount = 5,
            productIds = listOf(200L, 201L, 100L, 202L)
        )
    )

    private val customers = listOf(
        AccountSummary(id = 111L, displayName = "Ray Sponsible"),
        AccountSummary(id = 222L, displayName = "John Smith")
    )

    private val products = listOf(
        ProductSummary(
            id = 100,
            thumbnail = PictureSummary(
                url = "https://img.com/100.png"
            )
        ),
        ProductSummary(
            id = 200,
            thumbnail = PictureSummary(
                url = "https://img.com/200.png"
            )
        ),
        ProductSummary(
            id = 201,
            thumbnail = PictureSummary(
                url = "https://img.com/201.png"
            )
        ),
        ProductSummary(
            id = 202,
            thumbnail = PictureSummary(
                url = "https://img.com/202.png"
            )
        ),
        ProductSummary(
            id = 203,
            thumbnail = PictureSummary(
                url = "https://img.com/203.png"
            )
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchOrderResponse(orders)).whenever(orderApi).searchOrders(any())
        doReturn(SearchAccountResponse(customers)).whenever(accountApi).searchAccount(any())
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())
    }

    @Test
    fun empty() {

    }

    @Test
    @Ignore
    fun merchant() {
        val account = createAccount(ACCOUNT_ID, business = true)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        val url = "http://localhost:$port/orders"
        assertEndpointEquals("/store/screens/orders/merchant.json", url)
    }

    @Test
    @Ignore
    fun customer() {
        val account = createAccount(ACCOUNT_ID, business = false)
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        val url = "http://localhost:$port/orders"
        assertEndpointEquals("/store/screens/orders/customer.json", url)
    }
}
