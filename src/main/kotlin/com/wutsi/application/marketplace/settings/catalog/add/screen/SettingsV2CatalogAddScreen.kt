package com.wutsi.application.marketplace.settings.catalog.add.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.flutter.sdui.Page
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/catalog/add")
class SettingsV2CatalogAddScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "/settings/2/catalog/add/pages/picture",
            "/settings/2/catalog/add/pages/editor"
        )
    }

    @PostMapping
    fun index() = Screen(
        id = com.wutsi.application.Page.SETTINGS_CATALOG_ADD,
        appBar = null,
        safe = true,
        child = PageView(
            children = PAGE_URLS.map {
                Page(url = urlBuilder.build(it))
            }
        )
    ).toWidget()
}
