package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.service.AccountService
import com.wutsi.application.shell.service.PaymentService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.tenant.dto.Tenant
import com.wutsi.platform.tenant.entity.ToggleName
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/account")
class SettingsAccountScreen(
    private val tenantProvider: TenantProvider,
    private val accountService: AccountService,
    private val paymentService: PaymentService
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val paymentMethods = accountService.getPaymentMethods(tenant)

        return Screen(
            id = Page.SETTINGS_ACCOUNT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.app-bar.title")
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Center,
                        child = toBalanceWidget(paymentMethods, tenant)
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = Container(
                            alignment = Alignment.TopCenter,
                            child = accountListWidget(paymentMethods, tenant)
                        )
                    )
                ),
                crossAxisAlignment = CrossAxisAlignment.center
            )
        ).toWidget()
    }

    private fun accountListWidget(paymentMethods: List<PaymentMethodSummary>, tenant: Tenant): WidgetAware {
        val children = mutableListOf<WidgetAware>()
        children.addAll(
            paymentMethods
                .map {
                    ListItem(
                        caption = formattedAccountNumber(it),
                        iconLeft = accountService.getLogoUrl(tenant, it),
                        iconRight = Theme.ICON_CHEVRON_RIGHT,
                        padding = 10.0,
                        action = Action(
                            type = Route,
                            url = urlBuilder.build("settings/account/profile?token=${it.token}")
                        )
                    )
                }
        )
        children.add(
            Container(
                padding = 20.0,
                alignment = Center,
                child = Button(
                    caption = getText("page.settings.account.button.add-account"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("settings/accounts/link")
                    )
                )
            )
        )
        return ListView(
            children = children,
            separatorColor = Theme.COLOR_DIVIDER,
            separator = true
        )
    }

    private fun toBalanceWidget(
        paymentMethods: List<PaymentMethodSummary>,
        tenant: Tenant
    ): WidgetAware {
        val balance = paymentService.getBalance(tenant)

        return Column(
            children = listOfNotNull(
                Container(
                    alignment = Center,
                    padding = 10.0,
                    child = MoneyText(
                        value = balance.value,
                        currency = tenant.currencySymbol,
                        color = Theme.COLOR_PRIMARY,
                        numberFormat = tenant.numberFormat
                    )
                ),
                toToolbarWidget(balance.value > 0, paymentMethods)
            ),
            mainAxisAlignment = MainAxisAlignment.center,
            crossAxisAlignment = CrossAxisAlignment.center,
            mainAxisSize = MainAxisSize.min
        )
    }

    private fun toToolbarWidget(
        hasBalance: Boolean,
        paymentMethods: List<PaymentMethodSummary>
    ): WidgetAware {
        if (paymentMethods.isEmpty())
            return Container()

        val buttons = mutableListOf<WidgetAware>()
        if (togglesProvider.isToggleEnabled(ToggleName.CASHIN))
            buttons.add(
                toToolbarButton(
                    caption = getText("page.settings.account.button.add-cash"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                )
            )

        if (hasBalance && togglesProvider.isToggleEnabled(ToggleName.CASHOUT))
            buttons.add(
                toToolbarButton(
                    caption = getText("page.settings.account.button.cash-out"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashout")
                    )
                )
            )

        if (togglesProvider.isToggleEnabled(ToggleName.TRANSACTION_HISTORY))
            buttons.add(
                toToolbarButton(
                    caption = getText("page.settings.account.button.history"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "history")
                    )
                )
            )

        return Row(
            mainAxisAlignment = MainAxisAlignment.spaceAround,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = buttons
        )
    }

    private fun toToolbarButton(caption: String, action: Action): WidgetAware =
        Button(
            type = ButtonType.Text,
            caption = caption,
            stretched = false,
            padding = 10.0,
            action = action
        )
}
