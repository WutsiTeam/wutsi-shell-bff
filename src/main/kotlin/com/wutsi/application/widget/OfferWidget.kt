package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.util.StringUtil
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
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country

class OfferWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val thumbnailUrl: String?,
    private val action: Action,
    private val margin: Double = 10.0,
    private val imageService: ImageService
) : CompositeWidgetAware() {
    companion object {
        private const val PICTURE_HEIGHT = 150.0
        private const val PICTURE_ASPECT_RATIO_WIDTH = 4.0
        private const val PICTURE_ASPECT_RATIO_HEIGHT = 4.0

        fun of(product: Product, country: Country, action: Action, imageService: ImageService) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnail?.url,
            action = action,
            imageService = imageService
        )

        fun of(product: ProductSummary, country: Country, action: Action, imageService: ImageService) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnailUrl,
            action = action,
            imageService = imageService
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
                        child = Image(
                            url = resize(it),
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

    private fun resize(url: String): String =
        imageService.transform(
            url,
            Transformation(
                focus = Focus.AUTO,
                dimension = Dimension(height = PICTURE_HEIGHT.toInt()),
                aspectRatio = com.wutsi.platform.core.image.AspectRatio(
                    width = PICTURE_ASPECT_RATIO_WIDTH.toInt(),
                    height = PICTURE_ASPECT_RATIO_HEIGHT.toInt()
                )
            )
        )
}
