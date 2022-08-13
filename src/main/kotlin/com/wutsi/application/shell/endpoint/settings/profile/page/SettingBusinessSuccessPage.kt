package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping

class SettingBusinessSuccessPage : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Container(
            padding = 10.0,
            child = Form(
                children = listOf(
                    Container(padding = 20.0),
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.business.congratulations"),
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_X_LARGE,
                        )
                    ),
                    Container(
                        alignment = Alignment.Center,
                        padding = 20.0,
                        child = Icon(
                            code = Theme.ICON_CHECK_CIRCLE,
                            size = 80.0,
                            color = Theme.COLOR_SUCCESS
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = Button(
                            caption = getText("page.settings.business.button.ok"),
                            action = Action(
                                type = ActionType.Route,
                                url = "route:/~"
                            )
                        ),
                    ),
                )
            )
        ).toWidget()
    }
}
