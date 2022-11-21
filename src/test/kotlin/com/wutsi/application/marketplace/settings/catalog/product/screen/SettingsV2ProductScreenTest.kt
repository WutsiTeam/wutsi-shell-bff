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
import com.wutsi.marketplace.manager.dto.AddPictureRequest
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL
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
    fun index() {
        val product = Fixtures.createProduct(
            pictures = Fixtures.createPictureSummaryList(2)
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/catalog/product/screens/product.json", url())
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
}
