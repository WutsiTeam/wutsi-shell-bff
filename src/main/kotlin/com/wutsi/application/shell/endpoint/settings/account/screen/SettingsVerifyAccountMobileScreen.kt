package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/accounts/verify/mobile")
class SettingsVerifyAccountMobileScreen(
    private val service: AccountService,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val state = service.getSmsCodeEntity()
        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK_VERIFY,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.verify-account-mobile.app-bar.title")
            ),
            child = Column(
                children = listOf(
                    Container(
                        alignment = TopCenter,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.verify-account-mobile.sub-title"),
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                        )
                    ),
                    Text(
                        caption = formattedPhoneNumber(state.phoneNumber)!!,
                        alignment = TextAlignment.Center,
                        size = Theme.TEXT_SIZE_LARGE,
                    ),
                    Button(
                        caption = getText("page.verify-account-mobile.button.resend"),
                        type = ButtonType.Text,
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("commands/resend-sms-code")
                        )
                    ),
                    Container(
                        child = PinWithKeyboard(
                            name = "code",
                            hideText = true,
                            pinSize = 20.0,
                            keyboardButtonSize = 70.0,
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/verify-sms-code")
                            )
                        ),
                    )
                ),
            ),
        ).toWidget()
    }
}
