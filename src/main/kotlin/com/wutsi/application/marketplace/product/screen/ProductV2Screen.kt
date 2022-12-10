package com.wutsi.application.marketplace.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.util.StringUtil
import com.wutsi.application.widget.BusinessToolbarWidget
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.AspectRatio
import com.wutsi.flutter.sdui.CarouselSlider
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ExpandablePanel
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products/2")
class ProductV2Screen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractEndpoint() {
    companion object {
        const val PICTURE_HEIGHT = 250.0
        const val PICTURE_ASPECT_RATIO_WIDTH = 8.0
        const val PICTURE_ASPECT_RATIO_HEIGHT = 10.0
    }

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val product = marketplaceManagerApi.getProduct(id).product
        val merchant = membershipManagerApi.getMember(product.store.accountId).member
        val country = regulationEngine.country(merchant.country)

        // Screen
        return Screen(
            id = Page.PRODUCT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = merchant.displayName
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(),
            backgroundColor = Theme.COLOR_WHITE,
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOfNotNull(
                        toTitleWidget(product),
                        toPictureCarouselWidget(product),
                        Divider(color = Theme.COLOR_DIVIDER),
                        toPriceWidget(product, country),
                        Divider(color = Theme.COLOR_DIVIDER),
                        BusinessToolbarWidget.of(product, merchant, webappUrl),
                        Divider(color = Theme.COLOR_DIVIDER),
                        toSummaryWidget(product),
                        toAvailabilityWidget(product),
                        toDescription(product)
                    )
                )
            )
        ).toWidget()
    }

    private fun toTitleWidget(product: Product): WidgetAware =
        Container(
            padding = 10.0,
            background = Theme.COLOR_WHITE,
            child = Text(
                caption = StringUtil.capitalizeFirstLetter(product.title),
                size = Theme.TEXT_SIZE_LARGE,
                bold = true
            )
        )

    private fun toPictureCarouselWidget(product: Product): WidgetAware =
        CarouselSlider(
            viewportFraction = .9,
            enableInfiniteScroll = false,
            reverse = false,
            height = PICTURE_HEIGHT,
            children = product.pictures.map {
                AspectRatio(
                    aspectRatio = PICTURE_ASPECT_RATIO_WIDTH / PICTURE_ASPECT_RATIO_HEIGHT,
                    child = Image(
                        url = imageService.transform(
                            url = it.url,
                            transformation = Transformation(
                                dimension = Dimension(height = PICTURE_HEIGHT.toInt()),
                                aspectRatio = com.wutsi.platform.core.image.AspectRatio(
                                    width = PICTURE_ASPECT_RATIO_WIDTH.toInt(),
                                    height = PICTURE_ASPECT_RATIO_HEIGHT.toInt()
                                )
                            )
                        ),
                        height = PICTURE_HEIGHT
                    )
                )
            }
        )

    private fun toAvailabilityWidget(product: Product): WidgetAware? =
        product.quantity?.let {
            Container(
                padding = 10.0,
                child = Row(
                    children = listOf(
                        Icon(
                            code = if (it > 0) Theme.ICON_CHECK else Theme.ICON_CANCEL,
                            color = if (it > 0) Theme.COLOR_SUCCESS else Theme.COLOR_DANGER,
                            size = 16.0
                        ),
                        Container(padding = 5.0),
                        Text(
                            caption = if (it > 0)
                                getText("page.product.in-stock")
                            else
                                getText("page.product.out-of-stock")
                        )
                    )
                )
            )
        }

    private fun toPriceWidget(product: Product, country: Country): WidgetAware? =
        product.price?.let {
            Container(
                padding = 10.0,
                child = MoneyText(
                    currency = country.currencySymbol,
                    color = Theme.COLOR_PRIMARY,
                    valueFontSize = Theme.TEXT_SIZE_X_LARGE,
                    value = it.toDouble(),
                    numberFormat = country.monetaryFormat,
                    bold = true
                )
            )
        }

    private fun toSummaryWidget(product: Product): WidgetAware? =
        if (!product.summary.isNullOrEmpty()) {
            Container(
                padding = 10.0,
                child = Text(product.summary!!)
            )
        } else {
            null
        }

    private fun toDescription(product: Product): WidgetAware? =
        if (!product.description.isNullOrEmpty()) {
            Container(
                padding = 10.0,
                border = 1.0,
                borderColor = Theme.COLOR_GRAY_LIGHT,
                background = Theme.COLOR_WHITE,
                child = ExpandablePanel(
                    header = getText("page.product.product-details"),
                    expanded = Container(
                        padding = 10.0,
                        child = Text(product.description!!)
                    )
                )
            )
        } else {
            null
        }
}
