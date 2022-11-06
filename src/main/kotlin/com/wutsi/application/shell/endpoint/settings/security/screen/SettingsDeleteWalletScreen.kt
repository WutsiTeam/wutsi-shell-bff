package com.wutsi.application.shell.endpoint.settings.security.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/security/delete-wallet")
class SettingsDeleteWalletScreen : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        return Screen(
            id = Page.SETTINGS_SECURITY_DELETE_WALLET,
            appBar = AppBar(
                elevation = 0.0,
                title = getText("page.settings.delete-wallet.app-bar.title"),
                foregroundColor = Theme.COLOR_BLACK,
                backgroundColor = Theme.COLOR_WHITE
            ),
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Row(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = Icon(
                                        code = Theme.ICON_CANCEL,
                                        color = Theme.COLOR_DANGER,
                                        size = 32.0
                                    )
                                ),
                                Container(
                                    alignment = Alignment.CenterLeft,
                                    child = Text(
                                        caption = getText("page.settings.delete-wallet.confirmation"),
                                        alignment = TextAlignment.Center,
                                        size = Theme.TEXT_SIZE_LARGE,
                                        bold = true
                                    )
                                )
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Container(
                                alignment = Alignment.CenterLeft,
                                padding = 10.0,
                                child = Text(getText("page.settings.delete-wallet.sub-title"))
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.start,
                                children = IntRange(1, 5).map {
                                    Container(
                                        alignment = Alignment.CenterLeft,
                                        padding = 10.0,
                                        child = Text(getText("page.settings.delete-wallet.impact-$it"))
                                    )
                                }
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.settings.delete-wallet.button.delete"),
                                action = Action(
                                    type = ActionType.Route,
                                    url = urlBuilder.build(loginUrl, deleteActionUrl(user))
                                )
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                type = ButtonType.Text,
                                caption = getText("page.settings.delete-wallet.button.not-now"),
                                action = Action(
                                    type = ActionType.Route,
                                    url = "route:/.."
                                )
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }

    private fun deleteActionUrl(me: Account): String {
        return "/login?phone=" + encodeURLParam(me.phone!!.number) +
            "&screen-id=" + Page.SETTINGS_SECURITY_DELETE_WALLET_PIN +
            "&title=" + encodeURLParam(getText("page.settings.delete-wallet.app-bar.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.delete-wallet.pin")) +
            "&auth=false" +
            "&return-to-route=false" +
            "&return-url=" + encodeURLParam(urlBuilder.build("commands/delete-wallet"))
    }
}
