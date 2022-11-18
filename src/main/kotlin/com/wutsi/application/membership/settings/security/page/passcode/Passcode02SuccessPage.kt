package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode/pages/success")
class Passcode02SuccessPage : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget =
        Container(
            alignment = Center,
            padding = 20.0,
            child = Column(
                children = listOfNotNull(
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Icon(
                            code = Theme.ICON_CHECK,
                            size = 80.0,
                            color = Theme.COLOR_SUCCESS
                        )
                    ),
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.passcode.success.sub-title"),
                            alignment = TextAlignment.Center
                        )
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Button(
                        id = "ok",
                        caption = getText("page.settings.passcode.button.done"),
                        action = gotoPreviousScreen()
                    )
                )
            )
        ).toWidget()
}
