package com.wutsi.application.checkout.order.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import com.wutsi.checkout.manager.dto.GetOrderResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class OrderV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(id: String) = "http://localhost:$port${Page.getOrderUrl()}?id=$id"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        member = Fixtures.createMember(id = MEMBER_ID, businessId = 333, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(anyOrNull())

        val business = Fixtures.createBusiness(member.businessId!!, member.id)
        doReturn(GetBusinessResponse(business)).whenever(checkoutManagerApi).getBusiness(any())

        val order = Fixtures.createOrder("1111", member.businessId!!, member.id)
        doReturn(GetOrderResponse(order)).whenever(checkoutManagerApi).getOrder(any())
    }

    @Test
    fun index() {
        assertEndpointEquals("/checkout/order/screens/order.json", url("1111"))
    }
}
