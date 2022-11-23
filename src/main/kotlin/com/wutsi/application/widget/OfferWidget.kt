package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AspectRatio
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.regulation.Country

class OfferWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val thumbnailUrl: String?,
    private val action: Action,
    private val margin: Double = 10.0
) : CompositeWidgetAware() {
    companion object {
        const val PICTURE_HEIGHT = 150.0
        const val PICTURE_ASPECT_RATIO_WIDTH = 4.0
        const val PICTURE_ASPECT_RATIO_HEIGHT = 4.0

        fun of(product: Product, country: Country, action: Action) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnail?.url,
            action = action
        )

        fun of(product: ProductSummary, country: Country, action: Action) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnailUrl,
            action = action
        )
    }

    override fun toWidgetAware(): WidgetAware =
        Container(
            margin = margin,
            action = action,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    toThumbnailWidget(),
                    toInfoWidget()
                )
            )
        )

    private fun toThumbnailWidget(): WidgetAware? =
        thumbnailUrl?.let {
            ClipRRect(
                borderRadius = 5.0,
                child = AspectRatio(
                    aspectRatio = PICTURE_ASPECT_RATIO_WIDTH / PICTURE_ASPECT_RATIO_HEIGHT,
                    child = Container(
                        alignment = Alignment.Center,
                        background = Theme.COLOR_GRAY_LIGHT,
                        borderColor = Theme.COLOR_GRAY,
                        child = Image(
                            url = it,
                            fit = BoxFit.fitHeight
                        )
                    )
                )
            )
        }

    private fun toInfoWidget(): WidgetAware = Column(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.start,
        children = listOfNotNull(
            Container(padding = 5.0),
            Text(
                caption = StringUtil.capitalizeFirstLetter(title),
                overflow = TextOverflow.Elipsis,
                maxLines = 2,
                bold = true
            ),
            Container(padding = 5.0),
            price?.let {
                toPriceWidget(it)
            }
        )
    )

    private fun toPriceWidget(price: Long): WidgetAware =
        MoneyText(
            color = Theme.COLOR_PRIMARY,
            value = price.toDouble(),
            currency = country.currencySymbol,
            numberFormat = country.monetaryFormat,
            valueFontSize = Theme.TEXT_SIZE_DEFAULT,
            currencyFontSize = Theme.TEXT_SIZE_SMALL
        )
}
