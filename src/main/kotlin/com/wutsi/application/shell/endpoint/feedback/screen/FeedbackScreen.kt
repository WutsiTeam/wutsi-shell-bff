package com.wutsi.application.shell.endpoint.feedback.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feedback")
class FeedbackScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget = Screen(
        id = Page.FEEDBACK,
        appBar = AppBar(
            elevation = 0.0,
            backgroundColor = Theme.COLOR_WHITE,
            foregroundColor = Theme.COLOR_BLACK,
            title = getText("page.feedback.app-bar.title")
        ),
        child = Column(
            children = listOf(
                Center(
                    child = Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            caption = getText("page.feedback.message")
                        )
                    ),
                ),
                Divider(color = Theme.COLOR_DIVIDER),
                Form(
                    children = listOf(
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "message",
                                maxLines = 10,
                                caption = getText("page.feedback.input"),
                                required = true
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "submit",
                                type = InputType.Submit,
                                caption = getText("page.feedback.button.submit"),
                                action = Action(
                                    type = ActionType.Command,
                                    url = urlBuilder.build("commands/send-feedback")
                                )
                            )
                        )
                    )
                ),
            ),
        )
    ).toWidget()
}
