package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.service.AccountService
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
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
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/account/delete")
class SettingsAccountDeleteScreen(
    private val tenantProvider: TenantProvider,
    private val accountApi: WutsiAccountApi,
    private val accountService: AccountService
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam token: String): Widget {
        val tenant = tenantProvider.get()
        val paymentMethod = accountApi.getPaymentMethod(securityContext.currentAccountId(), token).paymentMethod
        val logoUrl = accountService.getLogoUrl(tenant, paymentMethod)
        val user = securityContext.currentAccount()

        return Screen(
            id = Page.SETTINGS_ACCOUNT_DELETE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.delete.app-bar.title")
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
                                        code = Theme.ICON_WARNING,
                                        color = Theme.COLOR_DANGER,
                                        size = 32.0
                                    )
                                ),
                                Container(
                                    alignment = Alignment.CenterLeft,
                                    child = Text(
                                        caption = getText("page.settings.account.delete.confirmation"),
                                        alignment = TextAlignment.Center,
                                        size = Theme.TEXT_SIZE_LARGE,
                                        bold = true
                                    )
                                )
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Row(
                                mainAxisAlignment = MainAxisAlignment.center,
                                crossAxisAlignment = CrossAxisAlignment.center,
                                children = listOfNotNull(
                                    logoUrl?.let {
                                        Image(
                                            width = 32.0,
                                            height = 32.0,
                                            url = it
                                        )
                                    },
                                    Container(padding = 5.0),
                                    Text(accountService.getNamePaymentMethodName(tenant, paymentMethod))
                                )
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Container(
                                alignment = Alignment.CenterLeft,
                                padding = 10.0,
                                child = Text(getText("page.settings.account.delete.sub-title"))
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.start,
                                children = IntRange(1, 3).map {
                                    Container(
                                        alignment = Alignment.CenterLeft,
                                        padding = 10.0,
                                        child = Text(getText("page.settings.account.delete.impact-$it"))
                                    )
                                }
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.settings.account.delete.button.delete"),
                                action = Action(
                                    type = ActionType.Route,
                                    url = urlBuilder.build(loginUrl, deleteActionUrl(user, token)),
                                    replacement = true
                                )
                            )
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                type = ButtonType.Text,
                                caption = getText("page.settings.account.delete.button.not-now"),
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

    private fun deleteActionUrl(me: Account, token: String): String {
        return "/login?phone=" + encodeURLParam(me.phone!!.number) +
            "&screen-id=" + Page.SETTINGS_ACCOUNT_DELETE_PIN +
            "&title=" + encodeURLParam(getText("page.settings.account.delete.app-bar.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.account.delete.pin")) +
            "&auth=false" +
            "&return-to-route=false" +
            "&return-url=" + encodeURLParam(urlBuilder.build("commands/delete-account?token=$token"))
    }
}
