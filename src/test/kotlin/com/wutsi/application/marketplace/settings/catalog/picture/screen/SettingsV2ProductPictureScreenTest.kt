package com.wutsi.application.marketplace.settings.catalog.picture.screen

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2ProductPictureScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val pictureId = 111L
    private val url = "http://www.gom.com/1.png"

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsCatalogUrl()}/picture$action?id=$pictureId&url=$url"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/catalog/picture/screens/picture.json", url())

    @Test
    fun delete() {
        val response = rest.postForEntity(url("/delete"), null, Action::class.java)

        // THEN
        verify(marketplaceManagerApi).deletePicture(
            pictureId
        )

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
    }
}
