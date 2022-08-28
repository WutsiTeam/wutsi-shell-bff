package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business")
class SettingsBusinessScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_BUSINESS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                automaticallyImplyLeading = false
            ),
            child = PageView(
                children = listOf(
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/start")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/display-name")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/biography")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/category")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/city")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/street")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/whatsapp")),
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build("/settings/business/pages/confirm"))
                )
            )
        ).toWidget()
    }
}
