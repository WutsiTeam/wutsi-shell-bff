package com.wutsi.application.membership.settings.security.screen

import com.wutsi.application.AbstractSecuredEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.membership.manager.dto.Member
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security")
class SecurityScreen : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val me = getCurrentMember()
        return Screen(
            id = Page.SECURITY,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.security.app-bar.title")
            ),
            child = ListView(
                separatorColor = Theme.COLOR_DIVIDER,
                separator = true,
                children = listOf(
                    ListItem(
                        caption = getText("page.settings.security.list-item.change-pin.caption"),
                        action = Action(
                            type = Route,
                            url = urlBuilder.build(getChangePasscodeUrl(me))
                        ),
                        trailing = Icon(
                            code = Theme.ICON_CHEVRON_RIGHT,
                            size = 24.0
                        ),
                        leading = Icon(
                            code = Theme.ICON_PIN,
                            size = 24.0,
                            color = Theme.COLOR_PRIMARY
                        )
                    ),
                    ListItem(
                        caption = getText("page.settings.security.list-item.delete-wallet.caption"),
                        action = Action(
                            type = Route,
                            url = urlBuilder.build("/security/delete-wallet")
                        ),
                        trailing = Icon(
                            code = Theme.ICON_CHEVRON_RIGHT,
                            size = 24.0
                        ),
                        leading = Icon(
                            code = Theme.ICON_CANCEL,
                            size = 24.0,
                            color = Theme.COLOR_DANGER
                        )
                    )
                )
            )
        ).toWidget()
    }

    private fun getChangePasscodeUrl(me: Member): String {
        return "/login?phone=" + encodeURLParam(me.phoneNumber) +
            "&title=" + encodeURLParam(getText("page.settings.security.pin-login.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.security.pin-login.sub-title")) +
            "&auth=false" +
            "&return-to-route=true" +
            "&dark-mode=true" +
            "&return-url=" + encodeURLParam(urlBuilder.build("security/pin"))
    }
}
