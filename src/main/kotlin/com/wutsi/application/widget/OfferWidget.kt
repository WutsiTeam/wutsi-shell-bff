package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.StringUtil
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AspectRatio
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
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
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OfferWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val thumbnailUrl: String?,
    private val action: Action,
    private val margin: Double = 10.0,
    private val imageService: ImageService,
    private val eventStartDate: String? = null,
    private val eventMeetingProviderLogoUrl: String? = null,
    private val eventMeetingProviderName: String? = null
) : CompositeWidgetAware() {
    companion object {
        private const val PICTURE_HEIGHT = 150.0
        private const val PICTURE_ASPECT_RATIO_WIDTH = 4.0
        private const val PICTURE_ASPECT_RATIO_HEIGHT = 4.0

        fun of(
            product: Product,
            country: Country,
            action: Action,
            imageService: ImageService,
            timezoneId: String?
        ) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnail?.url,
            action = action,
            imageService = imageService,
            eventStartDate = if (product.type == ProductType.EVENT.name) {
                product.event?.starts?.let {
                    convert(it, timezoneId).format(DateTimeFormatter.ofPattern(country.dateTimeFormat))
                }
            } else {
                null
            },
            eventMeetingProviderLogoUrl = if (product.type == ProductType.EVENT.name) {
                product.event?.meetingProvider?.logoUrl
            } else {
                null
            },
            eventMeetingProviderName = if (product.type == ProductType.EVENT.name) {
                product.event?.meetingProvider?.name
            } else {
                null
            }
        )

        fun of(
            product: ProductSummary,
            country: Country,
            action: Action,
            imageService: ImageService,
            timezoneId: String?
        ) = OfferWidget(
            title = product.title,
            price = product.price,
            country = country,
            thumbnailUrl = product.thumbnailUrl,
            action = action,
            imageService = imageService,
            eventStartDate = if ((product.type == ProductType.EVENT.name) && (product.event != null)) {
                product.event!!.starts?.let {
                    convert(it, timezoneId).format(DateTimeFormatter.ofPattern(country.dateTimeFormat))
                }
            } else {
                null
            },
            eventMeetingProviderLogoUrl = if (product.type == ProductType.EVENT.name) {
                product.event?.meetingProvider?.logoUrl
            } else {
                null
            },
            eventMeetingProviderName = if (product.type == ProductType.EVENT.name) {
                product.event?.meetingProvider?.name
            } else {
                null
            }
        )

        private fun convert(date: OffsetDateTime, timezoneId: String?): OffsetDateTime =
            if (timezoneId == null) {
                date
            } else {
                DateTimeUtil.convert(date, timezoneId)
            }
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
            eventStartDate?.let {
                Container(padding = 5.0)
            },
            eventStartDate?.let {
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Icon(
                            size = 16.0,
                            code = Theme.ICON_CALENDAR
                        ),
                        Container(padding = 2.0),
                        Text(it)
                    )
                )
            },

            eventMeetingProviderName?.let {
                Container(padding = 2.0)
            },
            eventMeetingProviderName?.let {
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOfNotNull(
                        if (eventMeetingProviderLogoUrl != null) {
                            Image(
                                width = 16.0,
                                height = 16.0,
                                url = eventMeetingProviderLogoUrl
                            )
                        } else {
                            null
                        },
                        if (eventMeetingProviderLogoUrl != null) {
                            Container(padding = 2.0)
                        } else {
                            null
                        },
                        Text(it)
                    )
                )
            },
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
