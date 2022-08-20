package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/whatsapp")
class SettingsProfileWhatsappScreen : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "whatsapp"

    override fun getPageId() = Page.SETTINGS_PROFILE_WHATSAPP

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.whatsapp,
        type = InputType.Phone,
        maxLength = 30
    )
}
