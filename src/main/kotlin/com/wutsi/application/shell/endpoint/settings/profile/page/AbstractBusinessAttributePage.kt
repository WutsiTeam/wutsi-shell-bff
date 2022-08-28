package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping

abstract class AbstractBusinessAttributePage : AbstractQuery() {
    abstract fun getAttributeName(): String
    abstract fun getInputWidget(account: Account): WidgetAware
    abstract fun getPageIndex(): Int

    open fun getDescription(account: Account): String =
        getText("page.settings.profile.attribute.${getAttributeName()}.description")

    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        return Container(
            id = Page.SETTINGS_BUSINESS + "." + getPageIndex(),
            padding = 10.0,
            child = Form(
                children = listOfNotNull(
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
                                url = urlBuilder.build("commands/update-business-attribute?name=${getAttributeName()}&page=${getPageIndex()}")
                            )
                        )
                    ),

                    if (getPageIndex() > 0)
                        Container(
                            padding = 10.0,
                            child = Button(
                                type = ButtonType.Text,
                                caption = getText("page.settings.profile.attribute.button.back"),
                                action = Action(
                                    type = ActionType.Page,
                                    url = "page:/${getPageIndex() - 1}"
                                )
                            )
                        )
                    else
                        null
                )
            )
        ).toWidget()
    }
}
