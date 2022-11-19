package com.wutsi.application.membership.settings.profile.screen

import com.wutsi.application.Page
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business")
class SettingsV2BusinessScreen : AbstractQuery() {
    companion object {
        private val PAGE_URLS = listOf(
            "${Page.getSettingsBusinessUrl()}/pages/start",
            "${Page.getSettingsBusinessUrl()}/pages/display-name",
            "${Page.getSettingsBusinessUrl()}/pages/category",
            "${Page.getSettingsBusinessUrl()}/pages/city",
            "${Page.getSettingsBusinessUrl()}/pages/whatsapp",
            "${Page.getSettingsBusinessUrl()}/pages/biography",
            "${Page.getSettingsBusinessUrl()}/pages/confirm",
            "${Page.getSettingsBusinessUrl()}/pages/success"
        )
    }

    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_BUSINESS,
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
