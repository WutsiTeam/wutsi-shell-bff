package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/email")
class SettingsProfileEmailScreen : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "email"

    override fun getPageId() = Page.SETTINGS_PROFILE_EMAIL

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.email,
        maxLength = 160,
        type = InputType.Email
    )

    override fun getSubmitUrl(name: String) = Action(
        type = ActionType.Command,
        url = urlBuilder.build("commands/start-email-verification")
    )
}
