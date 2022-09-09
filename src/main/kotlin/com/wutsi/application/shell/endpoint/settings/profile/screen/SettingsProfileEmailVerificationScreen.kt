package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/email/verification")
class SettingsProfileEmailVerificationScreen : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam email: String, @RequestParam token: String): Widget {
        return Screen(
            id = Page.SETTINGS_PROFILE_EMAIL_VERIFICATION,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.email.verification.app-bar.title")
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            alignment = TextAlignment.Center,
                            caption = getText(
                                "page.settings.profile.email.verification.title",
                                arrayOf(email)
                            ),
                            bold = true,
                            size = Theme.TEXT_SIZE_LARGE,
                            color = Theme.COLOR_PRIMARY
                        )
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            alignment = TextAlignment.Center,
                            caption = getText(
                                "page.settings.profile.email.verification.sub-title",
                                arrayOf(email)
                            )
                        )
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "code",
                            maxLength = 6,
                            type = InputType.Number,
                            required = true
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.email.verification.button.submit"),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/verify-email"),
                                parameters = mapOf(
                                    "email" to email,
                                    "token" to token
                                )
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }
}
