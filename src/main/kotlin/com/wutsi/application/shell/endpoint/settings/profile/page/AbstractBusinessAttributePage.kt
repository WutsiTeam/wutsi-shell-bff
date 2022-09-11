package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping

abstract class AbstractBusinessAttributePage : AbstractQuery() {
    abstract fun getAttributeName(): String
    abstract fun getInputWidget(account: Account): WidgetAware
    abstract fun getPageIndex(): Int

    open fun getDescription(account: Account): String =
        getText("page.settings.profile.attribute.${getAttributeName()}.description")

    protected open fun showSubmitButton(): Boolean =
        true

    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        val index = getPageIndex()

        return Container(
            id = Page.SETTINGS_BUSINESS + "." + getPageIndex(),
            padding = 10.0,
            child = Form(
                children = listOfNotNull(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.spaceBetween,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOf(
                            IconButton(
                                icon = Theme.ICON_ARROW_BACK,
                                color = Theme.COLOR_BLACK,
                                action = Action(
                                    type = ActionType.Page,
                                    url = "page:/${index - 1}"
                                )
                            ),
                            IconButton(
                                icon = Theme.ICON_CANCEL,
                                color = Theme.COLOR_BLACK,
                                action = Action(
                                    type = ActionType.Route,
                                    url = "route:/.."
                                )
                            )
                        )
                    ),

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

                    if (showSubmitButton())
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
                        )
                    else
                        null
                )
            )
        ).toWidget()
    }
}
