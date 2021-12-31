package com.wutsi.application.shell.endpoint.scan.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.QrView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scan")
class ScanScreen(
    private val urlBuilder: URLBuilder,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SCAN,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.scan.app-bar.title"),
            ),
            child = Flexible(
                child = QrView(
                    submitUrl = urlBuilder.build("commands/scan/process")
                )
            ),
        ).toWidget()
    }
}
