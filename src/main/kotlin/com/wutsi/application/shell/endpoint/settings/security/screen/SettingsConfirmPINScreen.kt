package com.wutsi.application.shell.endpoint.settings.security.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/security/confirm-pin")
class SettingsConfirmPINScreen : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam pin: String) = Screen(
        id = Page.SETTINGS_SECURITY_PIN_CONFIRM,
        backgroundColor = Theme.COLOR_PRIMARY,
        appBar = AppBar(
            elevation = 0.0,
            backgroundColor = Theme.COLOR_PRIMARY,
            foregroundColor = Theme.COLOR_WHITE,
            title = getText("page.settings.pin-confirm.app-bar.title")
        ),
        child = Container(
            alignment = Alignment.Center,
            child = Column(
                children = listOf(
                    Container(
                        alignment = Alignment.TopCenter,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.pin-confirm.sub-title"),
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                            color = Theme.COLOR_WHITE,
                        )
                    ),
                    PinWithKeyboard(
                        name = "pin",
                        hideText = true,
                        maxLength = 6,
                        pinSize = 20.0,
                        color = Theme.COLOR_WHITE,
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("commands/confirm-pin"),
                            parameters = mapOf(
                                "pin" to pin
                            )
                        ),
                    ),
                )
            )
        )
    ).toWidget()
}
