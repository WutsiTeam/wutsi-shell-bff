package com.wutsi.application.marketplace.catalog.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    val storeId = 1111L
    val accountId = 222L
    val productId = 444L

    private fun url() = "http://localhost:$port${Page.getProfileUrl()}?id=$productId"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val product = Fixtures.createProduct(
            id = productId,
            storeId = storeId,
            pictures = listOf(
                Fixtures.createPictureSummary(id = 1),
                Fixtures.createPictureSummary(id = 2),
                Fixtures.createPictureSummary(id = 3),
                Fixtures.createPictureSummary(id = 4)
            )
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        val store = Fixtures.createStore(storeId, accountId)
        doReturn(GetStoreResponse(store)).whenever(marketplaceManagerApi).getStore(any())

        val account = Fixtures.createMember(
            id = accountId,
            storeId = storeId
        )
        doReturn(GetMemberResponse(account)).whenever(membershipManagerApi).getMember(anyOrNull())
    }

    @Test
    fun products() {
        assertEndpointEquals("/marketplace/catalog/product/screens/product.json", url())
    }
}
