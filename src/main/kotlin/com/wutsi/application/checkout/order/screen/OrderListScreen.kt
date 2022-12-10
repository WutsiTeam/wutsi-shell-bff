package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.OrderWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.SearchOrderRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.DynamicWidget
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.platform.core.image.ImageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders/2/list")
class OrderListScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractSecuredEndpoint() {
    companion object {
        const val MAX_ORDERS = 100
    }

    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (!member.business || member.businessId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.order.list.tab.new").uppercase()),
                Text(getText("page.order.list.tab.in-progress").uppercase()),
                Text(getText("page.order.list.tab.closed").uppercase())
            )
        )
        return DefaultTabController(
            id = Page.PROFILE,
            length = tabs.tabs.size,
            child = Screen(
                id = Page.ORDER_LIST,
                backgroundColor = Theme.COLOR_WHITE,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_PRIMARY,
                    foregroundColor = Theme.COLOR_WHITE,
                    title = getText("page.order.list.app-bar.title"),
                    bottom = tabs
                ),
                bottomNavigationBar = createBottomNavigationBarWidget(),
                child = TabBarView(
                    children = listOfNotNull(
                        toTabView(arrayOf(OrderStatus.OPENED), business, false),
                        toTabView(arrayOf(OrderStatus.IN_PROGRESS), business, true),
                        toTabView(arrayOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED), business, true)
                    )
                )
            )
        ).toWidget()
    }

    @PostMapping("/fragment")
    fun fragment(@RequestParam(required = false) status: Array<OrderStatus> = emptyArray()): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (!member.business || member.businessId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        return toContentWidget(status, business).toWidget()
    }

    private fun toTabView(status: Array<OrderStatus>, business: Business, async: Boolean): WidgetAware {
        if (async) {
            val url = "${Page.getOrderListUrl()}/fragment?" +
                status.map { "status=$it" }.joinToString(separator = "&")
            return DynamicWidget(
                url = urlBuilder.build(url)
            )
        } else {
            return toContentWidget(status, business)
        }
    }

    private fun toContentWidget(status: Array<OrderStatus>, business: Business): WidgetAware {
        val orders = checkoutManagerApi.searchOrder(
            request = SearchOrderRequest(
                limit = MAX_ORDERS,
                businessId = business.id,
                status = status.map { it.name }
            )
        ).orders
        return Column(
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOfNotNull(
                Container(
                    padding = 10.0,
                    child = Text(
                        caption = if (orders.isEmpty()) {
                            getText("page.order.list.count-0")
                        } else if (orders.size == 1) {
                            getText("page.order.list.count-1")
                        } else {
                            getText("page.order.list.count-n", arrayOf(orders.size))
                        }
                    )
                ),
                Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                Flexible(
                    child = ListView(
                        separator = true,
                        separatorColor = Theme.COLOR_DIVIDER,
                        children = orders.map {
                            toOrderListItemWidget(it, business)
                        }
                    )
                )
            )
        )
    }

    private fun toOrderListItemWidget(order: OrderSummary, business: Business): WidgetAware =
        OrderWidget.of(
            order = order,
            country = regulationEngine.country(business.country),
            imageService = imageService,
            action = gotoUrl(
                url = urlBuilder.build(Page.getOrderUrl()),
                parameters = mapOf(
                    "id" to order.id
                )
            )
        )
}
