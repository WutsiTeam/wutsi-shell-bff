package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Positioned
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Stack
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.regulation.Country
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

class ProductCardWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val quantity: Int? = null,
    private val pictureUrl: String? = null,
    private val action: Action? = null,
    private val messages: MessageSource
) : CompositeWidgetAware() {
    companion object {
        const val PICTURE_HEIGHT = 120.0
        const val PICTURE_WIDTH = 50.0

        fun of(product: ProductSummary, country: Country, action: Action, messages: MessageSource) = ProductCardWidget(
            title = product.title,
            price = product.price,
            country = country,
            quantity = product.quantity,
            pictureUrl = product.thumbnailUrl,
            action = action,
            messages = messages
        )
    }

    override fun toWidgetAware(): WidgetAware = Container(
        borderColor = Theme.COLOR_GRAY_LIGHT,
        border = 1.0,
        action = action,
        margin = 5.0,
        child = Row(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                Flexible(
                    flex = 1,
                    child = PictureWidget(
                        url = pictureUrl ?: "",
                        border = 1.0,
                        height = PICTURE_HEIGHT,
                        width = PICTURE_WIDTH
                    )
                ),
                Flexible(
                    flex = 3,
                    child = Container(
                        height = PICTURE_HEIGHT,
                        child = Stack(
                            children = listOfNotNull(
                                toDescriptionSection(title, quantity),
                                toPriceSection(price, country)
                            )
                        )
                    )
                )
            )
        )
    )

    private fun toDescriptionSection(title: String, quantity: Int? = null): WidgetAware =
        Container(
            alignment = Alignment.TopLeft,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    Container(
                        padding = 5.0,
                        child = Text(
                            caption = title,
                            bold = true,
                            overflow = TextOverflow.Elipsis,
                            maxLines = 3,
                            size = Theme.TEXT_SIZE_DEFAULT
                        )
                    ),
                    quantity?.let {
                        Container(
                            padding = 5.0,
                            child = Text(
                                caption = getText("widget.product-card.quantity", arrayOf(it)),
                                color = if (it == 0) Theme.COLOR_DANGER else null,
                                bold = it == 0
                            )
                        )
                    }
                )
            )
        )

    private fun toPriceSection(price: Long?, country: Country): WidgetAware? =
        price?.let {
            Positioned(
                bottom = 10.0,
                right = 10.0,
                child = MoneyText(
                    color = Theme.COLOR_PRIMARY,
                    value = it.toDouble(),
                    currency = country.currencySymbol,
                    numberFormat = country.monetaryFormat,
                    valueFontSize = Theme.TEXT_SIZE_DEFAULT,
                    currencyFontSize = Theme.TEXT_SIZE_SMALL,
                    bold = true
                )
            )
        }

    private fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, LocaleContextHolder.getLocale())
}
