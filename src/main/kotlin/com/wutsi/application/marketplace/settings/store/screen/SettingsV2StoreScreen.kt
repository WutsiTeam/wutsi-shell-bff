package com.wutsi.application.marketplace.settings.store.screen

import com.wutsi.application.Page
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store")
class SettingsV2StoreScreen : AbstractQuery() {
    companion object {
        private val PAGE_URLS = listOf(
            "${Page.getSettingsStoreUrl()}/pages/start",
            "${Page.getSettingsStoreUrl()}/pages/success"
        )
    }

    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_STORE,
            safe = true,
            appBar = null,
            child = PageView(
                children = PAGE_URLS.map {
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build(it))
                }
            )
        ).toWidget()
    }
}
