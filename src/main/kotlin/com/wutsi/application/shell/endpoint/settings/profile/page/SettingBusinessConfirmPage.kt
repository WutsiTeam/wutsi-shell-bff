package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/confirm")
class SettingBusinessConfirmPage : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Column(
            id = Page.SETTINGS_BUSINESS + ".7",
            children = listOf(
                Container(padding = 40.0),
                Container(
                    alignment = Alignment.Center,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.settings.business.title"),
                        alignment = TextAlignment.Center,
                        size = Theme.TEXT_SIZE_X_LARGE
                    )
                ),
                Container(
                    alignment = Alignment.Center,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.settings.business.confirm"),
                        alignment = TextAlignment.Center
                    )
                ),
                Container(padding = 10.0),
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
                ),
                Container(
                    padding = 10.0,
                    child = Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.profile.attribute.button.back"),
                        action = Action(
                            type = ActionType.Page,
                            url = "page:/6"
                        )
                    )
                )
            )
        ).toWidget()
    }
}
