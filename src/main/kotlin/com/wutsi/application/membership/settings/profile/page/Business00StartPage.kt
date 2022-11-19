package com.wutsi.application.membership.settings.profile.page

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/start")
class Business00StartPage : AbstractBusinessPage() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.business.title")

    override fun getBody() = Container(
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
                toRowWidget(Theme.ICON_MONEY, "page.settings.business.why.payment")
            )
        )
    )

    override fun getButton() = Button(
        caption = getText("page.settings.business.button.next"),
        action = gotoPage(PAGE_INDEX + 1)
    )

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
                        foregroundColor = Theme.COLOR_PRIMARY,
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
