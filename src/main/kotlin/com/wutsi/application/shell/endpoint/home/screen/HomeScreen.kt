package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.ui.BottomNavigationButton
import com.wutsi.application.shared.ui.TransactionListItem
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.dto.SearchTransactionRequest
import com.wutsi.platform.payment.dto.TransactionSummary
import com.wutsi.platform.tenant.dto.Tenant
import com.wutsi.platform.tenant.entity.ToggleName
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val paymentApi: WutsiPaymentApi,
    private val accountApi: WutsiAccountApi,
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val me = securityContext.currentAccount()
        val balance = getBalance(me, tenant)
        val children = mutableListOf<WidgetAware>()

        // Balance - for business account only
        if (togglesProvider.isAccountEnabled())
            children.add(
                Container(
                    padding = 10.0,
                    alignment = Alignment.Center,
                    background = Theme.COLOR_PRIMARY,
                    child = Center(
                        child = MoneyText(
                            color = Theme.COLOR_WHITE,
                            value = balance.value,
                            currency = tenant.currencySymbol,
                            numberFormat = tenant.numberFormat
                        )
                    )
                )
            )

        // Primary Applications
        val primary = primaryButtons()
        if (primary.isNotEmpty())
            children.add(
                Container(
                    background = Theme.COLOR_PRIMARY,
                    child = Row(
                        mainAxisAlignment = spaceAround,
                        children = primary
                    )
                )
            )

        // Recent Transactions
        if (togglesProvider.isToggleEnabled(ToggleName.TRANSACTION_HISTORY)) {
            toTransactionsWidget(tenant)?.let { children.add(it) }
        }

        return Screen(
            id = Page.HOME,
            appBar = AppBar(
                title = me.displayName ?: "",
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                elevation = 0.0,
                automaticallyImplyLeading = false,
                leading = null,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build("settings")
                        )
                    )
                )
            ),
            child = SingleChildScrollView(
                child = Column(children = children)
            ),
            bottomNavigationBar = bottomNavigationBar(BottomNavigationButton.HOME)
        ).toWidget()
    }

    // Buttons
    private fun primaryButtons(): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (togglesProvider.isScanEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.scan"),
                    icon = Theme.ICON_SCAN,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build("scan")
                    )
                )
            )
        }

        if (togglesProvider.isAccountEnabled() && togglesProvider.isToggleEnabled(ToggleName.CASHIN))
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.cashin"),
                    icon = Theme.ICON_CASHIN,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                )
            )

        if (togglesProvider.isSendEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.send"),
                    icon = Theme.ICON_SEND,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(cashUrl, "send")
                    )
                )
            )

        if (togglesProvider.isAccountEnabled() && togglesProvider.isToggleEnabled(ToggleName.CASHOUT))
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.cashout"),
                    icon = Theme.ICON_CASHOUT,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(cashUrl, "cashout")
                    )
                )
            )

        if (togglesProvider.isPaymentEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.payment"),
                    icon = Theme.ICON_MONEY,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(cashUrl, "pay")
                    )
                )
            )

        return buttons
    }

    private fun primaryButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_WHITE,
        iconColor = Theme.COLOR_WHITE,
        padding = 1.0,
        action = action
    )

    private fun toTransactionsWidget(tenant: Tenant): WidgetAware? {
        val txs = findTransactions(3)
        if (txs.isEmpty())
            return null

        val accounts = findAccounts(txs)
        val paymentMethods = findPaymentMethods()
        val currentUser = securityContext.currentAccount()
        val children = mutableListOf<WidgetAware>()
        children.addAll(
            txs.flatMap {
                listOf(
                    TransactionListItem(
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build(cashUrl, "transaction?id=${it.id}")
                        ),
                        model = sharedUIMapper.toTransactionModel(
                            it,
                            currentUser = currentUser,
                            accounts = accounts,
                            paymentMethod = it.paymentMethodToken?.let { paymentMethods[it] },
                            tenant = tenant,
                            tenantProvider = tenantProvider
                        )
                    ),
                    Divider(height = 1.0, color = Theme.COLOR_DIVIDER)
                )
            }
        )
        children.add(
            Button(
                type = ButtonType.Text,
                padding = 5.0,
                caption = getText("page.home.button.more_transactions"),
                action = Action(
                    type = ActionType.Route,
                    url = urlBuilder.build(cashUrl, "history")
                )
            )
        )
        return Column(
            children = children
        )
    }

    private fun findTransactions(limit: Int): List<TransactionSummary> =
        paymentApi.searchTransaction(
            SearchTransactionRequest(
                accountId = securityContext.currentAccountId(),
                limit = limit,
                offset = 0
            )
        ).transactions

    private fun findPaymentMethods(): Map<String, PaymentMethodSummary> =
        accountApi.listPaymentMethods(securityContext.currentAccountId())
            .paymentMethods
            .map { it.token to it }.toMap()

    private fun findAccounts(txs: List<TransactionSummary>): Map<Long, AccountSummary> {
        val accountIds = txs.map { it.accountId }.toMutableSet()
        accountIds.addAll(txs.mapNotNull { it.recipientId })

        return accountApi.searchAccount(
            SearchAccountRequest(
                ids = accountIds.toList(),
                limit = accountIds.size
            )
        ).accounts.map { it.id to it }.toMap()
    }

    private fun getBalance(user: Account, tenant: Tenant): Money {
        try {
            val balance = paymentApi.getBalance(user.id).balance
            return Money(
                value = balance.amount,
                currency = balance.currency
            )
        } catch (ex: Throwable) {
            return Money(currency = tenant.currency)
        }
    }
}
