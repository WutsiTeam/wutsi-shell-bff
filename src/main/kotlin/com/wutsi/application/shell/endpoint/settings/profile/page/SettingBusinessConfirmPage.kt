package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/confirm")
class SettingBusinessConfirmPage : AbstractBusinessAttributePage() {
    override fun showSubmitButton(): Boolean = false
    override fun getAttributeName(): String = ""
    override fun getPageIndex(): Int = 7
    override fun getDescription(account: Account): String =
        getText("page.settings.business.title")

    override fun getInputWidget(account: Account): WidgetAware =
        Column(
            children = listOf(
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.settings.business.button.yes"),
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("commands/enable-business")
                        )
                    )
                ),
                Container(
                    padding = 10.0,
                    child = Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.business.button.no"),
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/~"
                        )
                    )
                )
            )
        )
}
