package com.wutsi.application.shell.endpoint.settings.security.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/security/pin/success")
class SettingsPINSuccessScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_SECURITY_PIN_SUCCESS,
            backgroundColor = Theme.COLOR_WHITE,
            safe = true,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                automaticallyImplyLeading = false
            ),
            child = Column(
                children = listOf(
                    Container(padding = 40.0),
                    Container(
                        alignment = Alignment.Center,
                        padding = 20.0,
                        child = Icon(
                            code = Theme.ICON_CHECK_CIRCLE,
                            size = 80.0,
                            color = Theme.COLOR_SUCCESS
                        )
                    ),
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.pin.success.sub-title"),
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_X_LARGE
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = Button(
                            type = ButtonType.Elevated,
                            caption = getText("page.settings.pin.success.button.submit"),
                            action = Action(
                                type = ActionType.Route,
                                url = "route:/.."
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }
}
