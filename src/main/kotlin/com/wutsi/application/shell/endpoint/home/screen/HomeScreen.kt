package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.start
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import com.wutsi.platform.tenant.entity.ToggleName
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val paymentApi: WutsiPaymentApi,
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val me = securityContext.currentAccount()
        val balance = getBalance(me, tenant)
        val children = mutableListOf<WidgetAware>()

        // Greetings
        children.add(
            Container(
                padding = 10.0,
                background = Theme.COLOR_PRIMARY,
                alignment = Alignment.TopLeft,
                child = Row(
                    children = listOf(
                        Text(
                            caption = getText("page.home.greetings", arrayOf(me.displayName)),
                            color = Theme.COLOR_WHITE
                        )
                    ),
                    mainAxisAlignment = start,
                    crossAxisAlignment = CrossAxisAlignment.start
                )
            )
        )

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

        // Secondary Apps
        children.addAll(
            toRows(applicationButtons(me), 4)
                .map {
                    Container(
                        child = Row(
                            children = it,
                            mainAxisAlignment = spaceAround
                        )
                    )
                }
        )

        return Screen(
            id = Page.HOME,
            appBar = null,
            child = SingleChildScrollView(
                child = Column(children = children)
            ),
            bottomNavigationBar = bottomNavigationBar(),
            safe = true
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

    private fun applicationButtons(me: Account): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()

        if (togglesProvider.isStoreEnabled())
            buttons.addAll(
                listOf(
                    applicationButton(
                        caption = getText("page.home.button.marketplace"),
                        icon = Theme.ICON_CART,
                        action = Action(
                            type = ActionType.Route,
                            url = "$storeUrl/marketplace"
                        )
                    )
                )
            )

        if (togglesProvider.isToggleEnabled(ToggleName.BUSINESS_ACCOUNT) && me.business && me.hasStore && togglesProvider.isOrderEnabled())
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.orders"),
                    icon = Theme.ICON_ORDERS,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(storeUrl, "orders?merchant=true")
                    )
                )
            )

        if (me.superUser && togglesProvider.isToggleEnabled(ToggleName.NEWS))
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.news"),
                    icon = Theme.ICON_NEWSPAPER,
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(newsUrl, "")
                    )
                )
            )

        return buttons
    }

    private fun applicationButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_PRIMARY,
        iconColor = Theme.COLOR_PRIMARY,
        padding = 1.0,
        action = action
    )

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

    private fun toRows(products: List<WidgetAware>, size: Int): List<List<WidgetAware>> {
        val rows = mutableListOf<List<WidgetAware>>()
        var cur = mutableListOf<WidgetAware>()
        products.forEach {
            cur.add(it)
            if (cur.size == size) {
                rows.add(cur)
                cur = mutableListOf()
            }
        }
        if (cur.isNotEmpty())
            rows.add(cur)
        return rows
    }
}
