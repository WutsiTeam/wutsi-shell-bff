package com.wutsi.application.shell.endpoint.scan.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.QrView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scan")
class ScanScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SCAN,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.scan.app-bar.title")
            ),
            child = Container(
                child = QrView(
                    submitUrl = urlBuilder.build("scan/viewer")
                )
            )
        ).toWidget()
    }
}
