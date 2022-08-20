package com.wutsi.application.shell.endpoint.settings.security.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.platform.account.dto.Account
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/security")
class SettingsSecurityScreen(
    @Value("\${wutsi.application.login-url}") private val loginUrl: String
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val me = securityContext.currentAccount()
        return Screen(
            id = Page.SETTINGS_SECURITY,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.security.app-bar.title")
            ),
            child = ListView(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Button(
                            caption = getText("page.settings.security.list-item.change-pin.caption"),
                            action = Action(
                                type = Route,
                                url = urlBuilder.build(loginUrl, loginUrlPath(me))
                            )
                        )
                    ),
                    Divider(color = Theme.COLOR_DIVIDER)
                )
            )
        ).toWidget()
    }

    private fun loginUrlPath(me: Account): String {
        return "?phone=" + encodeURLParam(me.phone!!.number) +
            "&screen-id=" + Page.SETTINGS_SECURITY_PIN_LOGIN +
            "&title=" + encodeURLParam(getText("page.settings.security.pin-login.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.security.pin-login.sub-title")) +
            "&auth=false" +
            "&return-to-route=true" +
            "&return-url=" + encodeURLParam(urlBuilder.build("settings/security/pin"))
    }
}
