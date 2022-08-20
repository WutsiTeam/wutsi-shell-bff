package com.wutsi.application.shell.endpoint.settings.picture.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/picture")
class SettingsPictureScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val me = securityContext.currentAccount()
        val pictureUrl = me.pictureUrl ?: ""
        return Screen(
            id = Page.SETTINGS_PICTURE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.picture.app-bar.title")
            ),
            child = Column(
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Image(
                            url = pictureUrl,
                            width = 256.0,
                            height = 256.0
                        )
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Input(
                        name = "file",
                        uploadUrl = urlBuilder.build("commands/upload-picture"),
                        type = InputType.Image,
                        imageSource = ImageSource.Camera,
                        caption = getText("page.settings.picture.camera"),
                        imageMaxWidth = 512,
                        imageMaxHeight = 512,
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/.."
                        )
                    ),
                    Input(
                        name = "file",
                        uploadUrl = urlBuilder.build("commands/upload-picture"),
                        type = InputType.Image,
                        imageSource = ImageSource.Gallery,
                        caption = getText("page.settings.picture.gallery"),
                        imageMaxWidth = 512,
                        imageMaxHeight = 512,
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/.."
                        )
                    ),
                    Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.picture.cancel"),
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/.."
                        )
                    ),
                    Divider(color = Theme.COLOR_DIVIDER)
                )
            )
        ).toWidget()
    }
}
