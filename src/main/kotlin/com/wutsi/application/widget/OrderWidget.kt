package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.widget.WidgetL10n.getText
import com.wutsi.checkout.manager.dto.Order
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.enums.OrderStatus
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country
import org.springframework.context.i18n.LocaleContextHolder
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OrderWidget(
    private val orderId: String,
    private val created: OffsetDateTime,
    private val customerName: String,
    private val totalPrice: Long,
    private val status: OrderStatus,
    private val productPictureUrls: List<String>,
    private val imageService: ImageService,
    private val country: Country,
    private val action: Action? = null
) : CompositeWidgetAware() {
    companion object {
        const val PRODUCT_PICTURE_SIZE = 48.0

        fun of(order: OrderSummary, country: Country, action: Action, imageService: ImageService): OrderWidget =
            OrderWidget(
                orderId = order.shortId,
                created = order.created,
                status = OrderStatus.valueOf(order.status),
                customerName = order.customerName,
                totalPrice = order.totalPrice,
                productPictureUrls = order.productPictureUrls,
                country = country,
                imageService = imageService,
                action = action
            )

        fun of(order: Order, country: Country, action: Action, imageService: ImageService): OrderWidget =
            OrderWidget(
                orderId = order.shortId,
                created = order.created,
                status = OrderStatus.valueOf(order.status),
                customerName = order.customerName,
                totalPrice = order.totalPrice,
                productPictureUrls = order.items.map { it.pictureUrl }.filterNotNull(),
                country = country,
                imageService = imageService,
                action = action
            )
    }

    override fun toWidgetAware(): WidgetAware {
        val moneyFormat = DecimalFormat(country.monetaryFormat)
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormat, LocaleContextHolder.getLocale())

        return Container(
            padding = 10.0,
            action = action,
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
                                        getText("widget.order.order-id", arrayOf(orderId)),
                                        bold = true,
                                        size = Theme.TEXT_SIZE_LARGE
                                    ),
                                    Container(padding = 5.0),
                                    Row(
                                        children = listOf(
                                            Icon(code = Theme.ICON_CALENDAR, size = 12.0),
                                            Container(padding = 5.0),
                                            Text(dateFormat.format(created))
                                        )
                                    ),
                                    Row(
                                        children = listOf(
                                            Icon(code = Theme.ICON_PERSON, size = 12.0),
                                            Container(padding = 5.0),
                                            Text(customerName)
                                        )
                                    )
                                )
                            ),
                            Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.end,
                                children = listOfNotNull(
                                    Text(
                                        caption = moneyFormat.format(totalPrice),
                                        bold = true,
                                        color = Theme.COLOR_PRIMARY
                                    ),
                                    toStatusBadge(status),
                                    Container(padding = 5.0),
                                    Row(
                                        mainAxisAlignment = MainAxisAlignment.end,
                                        crossAxisAlignment = CrossAxisAlignment.center,
                                        children = productPictureUrls.take(2).map {
                                            ClipRRect(
                                                borderRadius = 5.0,
                                                child = Image(
                                                    width = PRODUCT_PICTURE_SIZE,
                                                    height = PRODUCT_PICTURE_SIZE,
                                                    fit = BoxFit.fill,
                                                    url = imageService.transform(
                                                        url = it,
                                                        Transformation(
                                                            dimension = Dimension(
                                                                width = PRODUCT_PICTURE_SIZE.toInt(),
                                                                height = PRODUCT_PICTURE_SIZE.toInt()
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private fun toStatusBadge(status: OrderStatus): WidgetAware? =
        if (status == OrderStatus.CLOSED) {
            Text(
                color = Theme.COLOR_SUCCESS,
                caption = getText("order.status.CLOSED"),
                size = Theme.TEXT_SIZE_SMALL,
                bold = true
            )
        } else if (status == OrderStatus.CANCELLED) {
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
