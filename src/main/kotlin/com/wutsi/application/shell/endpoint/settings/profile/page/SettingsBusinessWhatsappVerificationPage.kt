package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("settings/business/pages/whatsapp-verification")
class SettingsBusinessWhatsappVerificationPage : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "code"
    override fun getPageIndex(): Int = 7

    override fun showSubmitButton(): Boolean =
        false

    override fun getInputWidget(account: Account): WidgetAware = PinWithKeyboard(
        name = "code",
        maxLength = 6,
        pinSize = 40.0,
        hideText = false,
        action = Action(
            type = ActionType.Command,
            url = urlBuilder.build("commands/verify-business-whatsapp")
        )
    )
}
