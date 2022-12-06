package com.wutsi.application.home

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.SearchOrderResponse
import com.wutsi.enums.OrderStatus
import com.wutsi.error.ErrorURN
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getHomeUrl()}"

    @Test
    fun index() = assertEndpointEquals("/home/screens/index.json", url())

    @Test
    fun business() {
        // GIVEN
        member = Fixtures.createMember(MEMBER_ID, businessId = 3333, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(MEMBER_ID)

        val orders = listOf(
            Fixtures.createOrderSummary(id = "11", totalPrice = 15000, status = OrderStatus.OPENED),
            Fixtures.createOrderSummary(id = "22", totalPrice = 25000, status = OrderStatus.OPENED)
        )
        doReturn(SearchOrderResponse(orders)).whenever(checkoutManagerApi).searchOrder(any())

        // THEN
        assertEndpointEquals("/home/screens/business.json", url())
    }

    @Test
    fun `redirect on onboard page if member not found`() {
        val ex = createNotFoundException(errorCode = ErrorURN.MEMBER_NOT_FOUND.urn)
        doThrow(ex).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/onboard/screens/index.json", url())
    }
}
