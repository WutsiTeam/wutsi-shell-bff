package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/accounts/link/bank")
class SettingsLinkAccountBankScreen(
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val user = securityContext.currentAccount()
        val financialInstitutions = tenant.financialInstitutions.sortedBy { it.name }
        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK_MOBILE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.link-account-bank.app-bar.title")
            ),
            child = SingleChildScrollView(
                child = Container(
                    alignment = Center,
                    child = Column(
                        children = listOf(
                            Container(
                                alignment = Center,
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.link-account-bank.title"),
                                    alignment = TextAlignment.Center,
                                    size = Theme.TEXT_SIZE_LARGE,
                                    bold = true
                                )
                            ),
                            Container(
                                alignment = TopCenter,
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.link-account-bank.sub-title"),
                                    alignment = TextAlignment.Center,
                                )
                            ),
                            Form(
                                children = listOf(
                                    Container(
                                        padding = 10.0,
                                        child = DropdownButton(
                                            name = "bankCode",
                                            required = true,
                                            hint = getText("page.link-account-bank.input.bank-code"),
                                            value = if (financialInstitutions.size == 1) financialInstitutions[0].code else null,
                                            children = financialInstitutions.map {
                                                DropdownMenuItem(
                                                    value = it.code,
                                                    caption = it.name,
                                                    icon = tenantProvider.logo(it)
                                                )
                                            }
                                        ),
                                    ),
                                    Container(
                                        padding = 10.0,
                                        child = Input(
                                            name = "number",
                                            required = true,
                                            caption = getText("page.link-account-bank.input.number"),
                                            maxLength = 30
                                        ),
                                    ),
                                    Container(
                                        padding = 10.0,
                                        child = Input(
                                            name = "ownerName",
                                            required = true,
                                            caption = getText("page.link-account-bank.input.owner-name"),
                                            maxLength = 100,
                                            value = user.displayName
                                        ),
                                    ),
                                    Container(
                                        padding = 10.0,
                                        child = Input(
                                            name = "command",
                                            type = Submit,
                                            caption = getText("page.link-account-bank.button.submit"),
                                            action = Action(
                                                type = Command,
                                                url = urlBuilder.build("commands/link-bank-account")
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
            )
        ).toWidget()
    }
}
