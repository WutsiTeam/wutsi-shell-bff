package com.wutsi.application.marketplace.settings.catalog.add.screen

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2CatalogAddScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsCatalogUrl()}/add"

    @Test
    fun index() = assertEndpointEquals("/marketplace/settings/catalog/add/screens/add.json", url())
}
