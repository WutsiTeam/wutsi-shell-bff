package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.profile.strength.ProfileStrengthContainer
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
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.start
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val paymentApi: WutsiPaymentApi,
    private val tenantProvider: TenantProvider,
    private val profileStrength: ProfileStrengthContainer,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val balance = getBalance(tenant)
        val me = securityContext.currentAccount()
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
            ),
        )

        // Balance
        if (togglesProvider.isAccountEnabled())
            children.add(
                Container(
                    alignment = Alignment.Center,
                    background = Theme.COLOR_PRIMARY,
                    child = Center(
                        child = MoneyText(
                            color = Theme.COLOR_WHITE,
                            value = balance.value,
                            currency = tenant.currencySymbol,
                            numberFormat = tenant.numberFormat,
                        )
                    ),
                )
            )

        // Primary Applications
        val primary = primaryButtons(me)
        if (primary.isNotEmpty())
            children.add(
                Container(
                    background = Theme.COLOR_PRIMARY,
                    child = Row(
                        mainAxisAlignment = spaceAround,
                        children = primary,
                    )
                )
            )

        // Secondary Apps
        children.addAll(
            toRows(applicationButtons(), 4)
                .map {
                    Container(
                        child = Row(
                            children = it,
                            mainAxisAlignment = spaceAround
                        )
                    )
                }
        )

        val strength = profileStrength.toWidget(me)
        if (strength != null)
            children.add(strength)

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
    private fun primaryButtons(me: Account): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (togglesProvider.isScanEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.scan"),
                    icon = Theme.ICON_SCAN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("scan")
                    ),
                ),
            )
        }

        if (togglesProvider.isAccountEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.cashin"),
                    icon = Theme.ICON_CASHIN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                ),
            )

        if (togglesProvider.isSendEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.send"),
                    icon = Theme.ICON_SEND,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "send")
                    )
                )
            )

        if (togglesProvider.isPaymentEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.payment"),
                    icon = Theme.ICON_MONEY,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "pay")
                    )
                )
            )

        if (me.business && me.hasStore && togglesProvider.isOrderEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.orders"),
                    icon = Theme.ICON_ORDERS,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(storeUrl, "orders")
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

    private fun applicationButtons(): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()

        if (togglesProvider.isStoreEnabled())
            buttons.addAll(
                listOf(
                    applicationButton(
                        caption = getText("page.home.button.marketplace"),
                        icon = Theme.ICON_CART,
                        action = Action(
                            type = Route,
                            url = "$storeUrl/marketplace"
                        )
                    )
                )
            )

        if (togglesProvider.isContactEnabled())
            buttons.add(
                applicationButton(
                    caption = getText("page.home.button.contact"),
                    icon = Theme.ICON_GROUP,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("contact")
                    )
                )
            )

        buttons.add(
            applicationButton(
                caption = getText("page.home.button.feedback"),
                icon = Theme.ICON_FEEDBACK,
                action = Action(
                    type = Route,
                    url = urlBuilder.build("feedback")
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

    private fun getBalance(tenant: Tenant): Money {
        try {
            val userId = securityContext.currentAccountId()
            val balance = paymentApi.getBalance(userId).balance
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
