package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("settings/business/pages/whatsapp")
class SettingsBusinessWhatsappPage : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "whatsapp"
    override fun getPageIndex(): Int = 6

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.whatsapp,
        type = InputType.Phone,
        maxLength = 30
    )
}
