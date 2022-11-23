package com.wutsi.application.marketplace.settings.catalog.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.AddPictureRequest
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SettingsV2ProductScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var regulationEngine: RegulationEngine

    @MockBean
    private lateinit var storageService: StorageService

    private val productId = 123L

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsCatalogUrl()}/product$action?id=$productId"

    @Test
    fun draft() {
        val product = Fixtures.createProduct(
            pictures = Fixtures.createPictureSummaryList(2),
            published = false
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/catalog/product/screens/product-draft.json", url())
    }

    @Test
    fun published() {
        val product = Fixtures.createProduct(
            pictures = Fixtures.createPictureSummaryList(2),
            published = true
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/catalog/product/screens/product-published.json", url())
    }

    @Test
    fun indexPictureLimit() {
        val product = Fixtures.createProduct(
            pictures = Fixtures.createPictureSummaryList(regulationEngine.maxPictures())
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/catalog/product/screens/product-picture-limit.json", url())
    }

    @Test
    fun upload() {
        // GIVEN
        val filename = "toto.png"
        val fileUrl = URL("http://www.wutsi.com/asset/1/$filename")
        doReturn(fileUrl).whenever(storageService).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        uploadFile(url("/upload"), "toto.png")

        // THEN
        val path = argumentCaptor<String>()
        verify(storageService).store(path.capture(), any(), eq("image/png"), anyOrNull(), anyOrNull())
        assertTrue(path.firstValue.startsWith("product/$productId/pictures/"))
        assertTrue(path.firstValue.endsWith(filename))

        verify(marketplaceManagerApi).addPicture(
            request = AddPictureRequest(
                productId = productId,
                url = fileUrl.toString()
            )
        )
    }

    @Test
    fun publish() {
        // WHEN
        val response = rest.postForEntity(url("/publish"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/2/catalog/product?id=123", action.url)

        verify(marketplaceManagerApi).publishProduct(productId)
    }

    @Test
    fun unpublish() {
        // WHEN
        val response = rest.postForEntity(url("/unpublish"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/2/catalog/product?id=123", action.url)

        verify(marketplaceManagerApi).unpublishProduct(productId)
    }

    @Test
    fun delete() {
        // WHEN
        val response = rest.postForEntity(url("/delete"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).deleteProduct(productId)
    }
}