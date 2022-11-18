package com.wutsi.application.membership.settings.profile.page

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/start")
class Business00StartPage : AbstractBusinessPage() {
    companion object {
        const val PAGE_INDEX = 0
    }

    @PostMapping
    fun index(): Widget {
        return Column(
            children = listOf(
                Row(
                    mainAxisAlignment = MainAxisAlignment.end,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
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
                Container(padding = 40.0),
                Container(
                    alignment = Alignment.Center,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.settings.business.title"),
                        alignment = TextAlignment.Center,
                        size = Theme.TEXT_SIZE_X_LARGE,
                        color = Theme.COLOR_PRIMARY,
                        bold = true
                    )
                ),
                Container(
                    padding = 20.0,
                    margin = 20.0,
                    border = 1.0,
                    borderColor = Theme.COLOR_PRIMARY,
                    background = Theme.COLOR_PRIMARY_LIGHT,
                    child = Column(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOf(
                            toRowWidget(Theme.ICON_STORE, "page.settings.business.why.store"),
                            toRowWidget(Theme.ICON_CHAT, "page.settings.business.why.chat"),
                            toRowWidget(Theme.ICON_ORDER, "page.settings.business.why.order"),
                            toRowWidget(Theme.ICON_MONEY, "page.settings.business.why.payment"),
                        )
                    )
                ),
                Container(padding = 10.0),
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.settings.business.button.next"),
                        action = gotoPage(PAGE_INDEX + 1)
                    )
                )
            )
        ).toWidget()
    }

    private fun toRowWidget(icon: String, text: String): WidgetAware =
        Row(
            children = listOf(
                Container(
                    padding = 5.0,
                    child = CircleAvatar(
                        child = Icon(
                            code = icon,
                            color = Theme.COLOR_PRIMARY,
                            size = 24.0
                        ),
                        backgroundColor = Theme.COLOR_WHITE,
                        radius = 16.0
                    )
                ),
                Container(
                    padding = 5.0,
                    child = Text(getText(text))
                )
            )
        )
}
