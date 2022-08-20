package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping

abstract class AbstractSettingsProfileAttributeScreen : AbstractQuery() {
    abstract fun getAttributeName(): String

    abstract fun getPageId(): String

    abstract fun getInputWidget(account: Account): WidgetAware

    open fun getDescription(account: Account): String =
        getText("page.settings.profile.attribute.${getAttributeName()}.description")

    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        val name = getAttributeName()
        return Screen(
            id = getPageId(),
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.attribute.$name")
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(getDescription(user))
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Container(
                        padding = 10.0,
                        child = getInputWidget(user)
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.attribute.button.submit"),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/update-profile-attribute?name=$name")
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }
}
