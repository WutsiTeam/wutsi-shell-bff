package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.shared.Theme
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
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.platform.core.image.ImageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

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
        val business = member.businessId?.let {
            checkoutManagerApi.getBusiness(it).business
        }
        val today = OffsetDateTime.now()
        val orders = business?.let {
            checkoutManagerApi.searchOrder(
                request = SearchOrderRequest(
                    businessId = business.id,
                    limit = MAX_ORDERS,
                    createdTo = today,
                    createdFrom = today.minusDays(31),
                    status = OrderStatus.values().toMutableList()
                        .filter { it != OrderStatus.UNKNOWN && it != OrderStatus.PENDING && it != OrderStatus.EXPIRED }
                        .map { it.name }
                )
            ).orders
        } ?: emptyList()

        return Screen(
            id = Page.ORDER_LIST,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.order.list.app-bar.title")
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(),
            child = Column(
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
                                toOrderListItemWidget(it, business!!)
                            }
                        )
                    )
                )
            )
        ).toWidget()
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
