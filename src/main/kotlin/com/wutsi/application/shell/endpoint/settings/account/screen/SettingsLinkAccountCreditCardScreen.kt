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
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/accounts/link/credit-card")
class SettingsLinkAccountCreditCardScreen(
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val user = securityContext.currentAccount()

        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK_CREDIT_CARD,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.link-account-credit-card.app-bar.title")
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = listOf(
                        Container(
                            alignment = Center,
                            padding = 10.0,
                            child = Text(
                                caption = getText("page.link-account-credit-card.title"),
                                alignment = TextAlignment.Center,
                                size = Theme.TEXT_SIZE_LARGE,
                                bold = true
                            )
                        ),
                        Form(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "number",
                                        type = InputType.Number,
                                        required = true,
                                        countries = tenant.countries,
                                        caption = getText("page.link-account-credit-card.input.number")
                                    )
                                ),
                                Container(
                                    padding = 10.0,
                                    child = DropdownButton(
                                        name = "expiryMonth",
                                        required = true,
                                        hint = getText("page.link-account-credit-card.input.expiry-month"),
                                        children = IntRange(1, 12)
                                            .map {
                                                DropdownMenuItem(
                                                    value = it.toString(),
                                                    caption = LocalDate.of(2020, it, 1)
                                                        .format(DateTimeFormatter.ofPattern("MMMM"))
                                                )
                                            }
                                    )
                                ),
                                Container(
                                    padding = 10.0,
                                    child = DropdownButton(
                                        stretched = false,
                                        name = "expiryMonth",
                                        required = true,
                                        hint = getText("page.link-account-credit-card.input.expiry-year"),
                                        children = IntRange(
                                            LocalDate.now().year,
                                            LocalDate.now().year + 4
                                        ).map {
                                            DropdownMenuItem(
                                                value = it.toString(),
                                                caption = it.toString()
                                            )
                                        }
                                    )
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "ownerName",
                                        required = true,
                                        caption = getText("page.link-account-credit-card.input.owner-name"),
                                        maxLength = 100,
                                        value = user.displayName
                                    )
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Row(
                                        children = tenant.creditCardTypes
                                            .mapNotNull { tenantProvider.logo(it) }
                                            .map {
                                                Image(
                                                    width = 32.0,
                                                    height = 32.0,
                                                    url = it
                                                )
                                            }
                                    )
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "command",
                                        type = Submit,
                                        caption = getText("page.link-account-credit-card.button.submit"),
                                        action = Action(
                                            type = Command,
                                            url = urlBuilder.build("commands/link-credit-card")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }
}
