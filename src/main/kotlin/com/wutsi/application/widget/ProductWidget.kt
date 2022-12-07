package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.ecommerce.catalog.entity.ProductStatus
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Chip
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Positioned
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Stack
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country

class ProductWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val quantity: Int? = null,
    private val pictureUrl: String? = null,
    private val action: Action? = null,
    private val status: String? = null,
    private val imageService: ImageService
) : CompositeWidgetAware() {
    companion object {
        private const val PICTURE_HEIGHT = 100.0
        private const val PICTURE_WIDTH = 100.0

        fun of(product: ProductSummary, country: Country, action: Action, imageService: ImageService) = ProductWidget(
            title = product.title,
            price = product.price,
            country = country,
            quantity = product.quantity,
            pictureUrl = product.thumbnailUrl,
            action = action,
            status = product.status,
            imageService = imageService
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
                    child = pictureUrl?.let {
                        ClipRRect(
                            child = Image(
                                url = resize(it),
                                height = PICTURE_HEIGHT,
                                width = PICTURE_WIDTH,
                                fit = BoxFit.fill
                            ),
                            borderRadius = 5.0
                        )
                    }
                ),
                Flexible(
                    flex = 3,
                    child = Container(
                        height = PICTURE_HEIGHT,
                        child = Stack(
                            children = listOfNotNull(
                                toDescriptionSection(),
                                toPriceSection()
                            )
                        )
                    )
                )
            )
        )
    )

    private fun toDescriptionSection(): WidgetAware =
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

    private fun toPriceSection(): WidgetAware? =
        price?.let {
            Positioned(
                bottom = 10.0,
                right = 10.0,
                child = Column(
                    children = listOfNotNull(
                        if (status == ProductStatus.DRAFT.name) {
                            Chip(
                                color = Theme.COLOR_WHITE,
                                backgroundColor = Theme.COLOR_GRAY,
                                caption = getText("widget.product-card.draft"),
                                fontSize = Theme.TEXT_SIZE_SMALL,
                                padding = 2.0
                            )
                        } else {
                            null
                        },
                        MoneyText(
                            color = Theme.COLOR_PRIMARY,
                            value = it.toDouble(),
                            currency = country.currencySymbol,
                            numberFormat = country.monetaryFormat,
                            valueFontSize = Theme.TEXT_SIZE_DEFAULT,
                            currencyFontSize = Theme.TEXT_SIZE_SMALL,
                            bold = true
                        )
                    )
                )
            )
        }

    private fun getText(key: String, args: Array<Any> = emptyArray()): String =
        WidgetL10n.getText(key, args)

    private fun resize(url: String): String =
        imageService.transform(
            url,
            Transformation(
                focus = Focus.AUTO,
                dimension = Dimension(height = PICTURE_HEIGHT.toInt())
            )
        )
}
