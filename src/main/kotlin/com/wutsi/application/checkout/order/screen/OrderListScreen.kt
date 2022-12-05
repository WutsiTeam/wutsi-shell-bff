package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.shared.Theme
import com.wutsi.application.util.SecurityUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.SearchOrderRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/orders/2/list")
class OrderListScreen(
    private val membershipManagerApi: MembershipManagerApi,
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractEndpoint() {
    companion object {
        const val MAX_ORDERS = 100
        const val PRODUCT_PICTURE_SIZE = 48.0
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

    private fun toOrderListItemWidget(order: OrderSummary, business: Business): WidgetAware {
        val country = regulationEngine.country(business.country)
        val moneyFormat = DecimalFormat(country.monetaryFormat)
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormat)

        return Container(
            padding = 10.0,
            action = gotoUrl(
                url = urlBuilder.build(Page.getOrderUrl()),
                parameters = mapOf(
                    "id" to order.id
                )
            ),
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.spaceBetween,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOf(
                            Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.start,
                                children = listOf(
                                    Text(
                                        getText("page.order.list.order-id", arrayOf(order.shortId)),
                                        bold = true,
                                        size = Theme.TEXT_SIZE_LARGE
                                    ),
                                    Container(padding = 5.0),
                                    Row(
                                        children = listOf(
                                            Icon(code = Theme.ICON_CALENDAR, size = 12.0),
                                            Container(padding = 5.0),
                                            Text(dateFormat.format(order.created))
                                        )
                                    ),
                                    Row(
                                        children = listOf(
                                            Icon(code = Theme.ICON_PERSON, size = 12.0),
                                            Container(padding = 5.0),
                                            Text(order.customerName)
                                        )
                                    )
                                )
                            ),
                            Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.end,
                                children = listOfNotNull(
                                    Text(
                                        caption = moneyFormat.format(order.totalPrice),
                                        bold = true,
                                        color = Theme.COLOR_PRIMARY,
                                    ),
                                    toStatusBadge(order)
                                )
                            )
                        )
                    ),
                    Row(
                        mainAxisAlignment = MainAxisAlignment.end,
                        crossAxisAlignment = CrossAxisAlignment.center,
                        children = order.productPictureUrls.map {
                            ClipRRect(
                                borderRadius = 5.0,
                                child = Image(
                                    url = imageService.transform(
                                        url = it,
                                        Transformation(
                                            dimension = Dimension(
                                                width = PRODUCT_PICTURE_SIZE.toInt(),
                                                height = PRODUCT_PICTURE_SIZE.toInt()
                                            )
                                        )
                                    ),
                                    width = PRODUCT_PICTURE_SIZE,
                                    height = PRODUCT_PICTURE_SIZE
                                )
                            )
                        }
                    )
                )
            )
        )
    }

    private fun toStatusBadge(order: OrderSummary): WidgetAware? =
        if (order.status == OrderStatus.CLOSED.name) {
            Text(
                color = Theme.COLOR_SUCCESS,
                caption = getText("order.status.CLOSED"),
                size = Theme.TEXT_SIZE_SMALL,
                bold = true
            )
        } else if (order.status == OrderStatus.CANCELLED.name) {
            Text(
                color = Theme.COLOR_DANGER,
                caption = getText("order.status.CANCELLED"),
                size = Theme.TEXT_SIZE_SMALL,
                bold = true
            )
        } else {
            null
        }
}
