package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.shared.Theme
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Order
import com.wutsi.checkout.manager.dto.OrderItem
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.TransactionType
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/orders/2")
class OrderV2Screen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractEndpoint() {
    companion object {
        const val PRODUCT_PICTURE_SIZE = 64.0
        const val PROVIDER_PICTURE_SIZE = 48.0
    }

    @PostMapping
    fun index(@RequestParam id: String): Widget {
        val order = checkoutManagerApi.getOrder(id).order
        val country = regulationEngine.country(order.business.country)
        val dateFormat = DateTimeFormatter.ofPattern(country.dateTimeFormat)
        val moneyFormat = DecimalFormat(country.monetaryFormat)

        return Screen(
            id = Page.ORDER,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.order.app-bar.title", arrayOf(order.shortId))
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(),
            child = SingleChildScrollView(

                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    children = listOfNotNull(
                        toCustomerWidget(order, dateFormat),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toItemListWidget(order, moneyFormat),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toPriceWidget(order, moneyFormat)
                    )
                )
            )
        ).toWidget()
    }

    private fun toCustomerWidget(order: Order, dateFormat: DateTimeFormatter) = Container(
        padding = 10.0,
        alignment = Alignment.Center,
        child = Column(
            mainAxisAlignment = MainAxisAlignment.center,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOf(
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = order.customerName,
                        bold = true,
                        size = Theme.TEXT_SIZE_X_LARGE
                    )
                ),
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = getText("page.order.ordered-on", arrayOf(dateFormat.format(order.created)))
                    )
                ),
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = getText("order.status.${order.status}"),
                        color = when (order.status) {
                            OrderStatus.OPENED.name -> Theme.COLOR_PRIMARY
                            OrderStatus.CANCELLED.name, OrderStatus.EXPIRED.name -> Theme.COLOR_DANGER
                            OrderStatus.CLOSED.name -> Theme.COLOR_SUCCESS
                            else -> null
                        }
                    )
                )
            )
        )
    )

    private fun toItemListWidget(order: Order, moneyFormat: DecimalFormat) = Container(
        padding = 10.0,
        child = Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = order.items.map { toItemWidget(it, moneyFormat) }
        )
    )

    private fun toItemWidget(item: OrderItem, monetaryFormat: DecimalFormat) = Row(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.center,
        children = listOfNotNull(
            Flexible(
                flex = 1,
                child = Container(
                    padding = 5.0,
                    margin = 5.0,
                    background = Theme.COLOR_GRAY_LIGHT,
                    borderRadius = 5.0,
                    child = Text(
                        caption = item.quantity.toString(),
                        color = Theme.COLOR_BLACK
                    )
                )
            ),
            item.pictureUrl?.let {
                Flexible(
                    flex = 3,
                    child = ClipRRect(
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
                )
            },
            Flexible(
                flex = 8,
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Container(
                            padding = 5.0,
                            child = Text(caption = item.title)
                        ),
                        Container(
                            padding = 5.0,
                            child = Text(
                                caption = getText("page.order.unit-price") +
                                    ": " +
                                    monetaryFormat.format(item.unitPrice)
                            )
                        )
                    )
                )
            )
        )
    )

    private fun toPriceWidget(order: Order, moneyFormat: DecimalFormat): WidgetAware {
        val tx = order.transactions
            .find { it.type == TransactionType.CHARGE.name && it.status == Status.SUCCESSFUL.name }

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOfNotNull(
                if (order.subTotalPrice != order.totalPrice) {
                    tableRow(getText("page.order.sub-total"), moneyFormat.format(order.subTotalPrice))
                } else {
                    null
                },
                if (order.totalDiscount > 0) {
                    tableRow(
                        getText("page.order.discount"),
                        "-" + moneyFormat.format(order.totalDiscount),
                        color = Theme.COLOR_SUCCESS
                    )
                } else {
                    null
                },
                tableRow(
                    getText("page.order.total"),
                    moneyFormat.format(order.totalPrice),
                    bold = true,
                    size = Theme.TEXT_SIZE_LARGE,
                    color = Theme.COLOR_PRIMARY
                ),
                tx?.let {
                    tableRow(
                        Row(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.center,
                            children = listOf(
                                Image(
                                    url = tx.paymentMethod.provider.logoUrl,
                                    width = PROVIDER_PICTURE_SIZE,
                                    height = PROVIDER_PICTURE_SIZE
                                ),
                                Container(padding = 5.0),
                                Text("..." + tx.paymentMethod.number.takeLast(4))
                            )
                        ),
                        ""
                    )
                }
            )
        )
    }

    private fun tableRow(
        name: String,
        value: String,
        bold: Boolean? = null,
        color: String? = null,
        size: Double? = null
    ) = tableRow(
        name = Text(name, size = size),
        value = value,
        bold = bold,
        color = color,
        size = size
    )

    private fun tableRow(
        name: WidgetAware,
        value: String,
        bold: Boolean? = null,
        color: String? = null,
        size: Double? = null
    ) = Row(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.center,
        children = listOf(
            Flexible(
                flex = 1,
                child = Container(
                    padding = 10.0,
                    child = name
                )
            ),
            Flexible(
                flex = 1,
                child = Container(
                    padding = 10.0,
                    child = Text(value, alignment = TextAlignment.Right, bold = bold, color = color, size = size)
                )
            )
        )
    )
}
