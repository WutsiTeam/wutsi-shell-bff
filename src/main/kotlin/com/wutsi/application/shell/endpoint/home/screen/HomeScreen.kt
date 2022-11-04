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
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.payment.WutsiPaymentApi
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
        val children = mutableListOf<WidgetAware>()

        // Recent Transactions
        if (togglesProvider.isToggleEnabled(ToggleName.TRANSACTION_HISTORY)) {
            toTransactionsWidget(tenant)?.let { children.add(it) }
        }

        return Screen(
            id = Page.HOME,
            appBar = AppBar(
                title = me.displayName ?: "",
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
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
}
