package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/facebook")
class SettingsProfileFacebookIdScreen : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "facebook-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_FACEBOOK_ID

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.facebookId,
        maxLength = 30
    )
}
