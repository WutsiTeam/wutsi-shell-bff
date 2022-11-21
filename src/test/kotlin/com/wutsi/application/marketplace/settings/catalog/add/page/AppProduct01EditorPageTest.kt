package com.wutsi.application.marketplace.settings.catalog.add.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.catalog.add.dto.SubmitProductRequest
import com.wutsi.application.marketplace.settings.catalog.add.entity.PictureEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL

internal class AppProduct01EditorPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var storageService: StorageService

    private val entity = PictureEntity(
        url = "http://www.google.ca/1.png"
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsCatalogUrl()}/add/pages/editor$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(any(), any<Class<*>>())
    }

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/catalog/home/pages/editor.json", url())

    @Test
    fun submit() {
        // GIVEN
        val filename = "toto.png"
        val fileUrl = URL("http://www.wutsi.com/asset/1/$filename")
        doReturn(fileUrl).whenever(storageService).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        val request = SubmitProductRequest(
            title = "Yo man",
            summary = "This looks awesome",
            price = 15000,
            quantity = "10"
        )
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        verify(marketplaceManagerApi).createProduct(
            request = CreateProductRequest(
                pictureUrl = entity.url,
                title = request.title,
                price = request.price,
                quantity = request.quantity.toInt(),
                summary = request.summary
            )
        )

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
    }
}
