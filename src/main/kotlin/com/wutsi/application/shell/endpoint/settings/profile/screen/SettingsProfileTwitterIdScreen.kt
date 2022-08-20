package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/twitter")
class SettingsProfileTwitterIdScreen : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "twitter-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_TWITTER_ID

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.twitterId,
        maxLength = 30
    )
}
