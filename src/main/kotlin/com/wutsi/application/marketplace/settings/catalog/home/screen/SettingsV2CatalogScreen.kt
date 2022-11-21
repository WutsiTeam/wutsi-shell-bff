package com.wutsi.application.marketplace.settings.catalog.home.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.widget.ProductCardWidget
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.platform.core.image.AspectRatio
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/catalog")
class SettingsV2CatalogScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember().member
        val products = marketplaceManagerApi.searchProduct(
            request = SearchProductRequest(
                storeId = member.storeId,
                limit = regulationEngine.maxProducts()
            )
        ).products

        return Screen(
            id = Page.SETTINGS_CATALOG,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.app-bar.title")
            ),
            floatingActionButton = if (products.size < regulationEngine.maxProducts()) {
                Button(
                    type = ButtonType.Floatable,
                    icon = Theme.ICON_ADD,
                    stretched = false,
                    iconColor = Theme.COLOR_WHITE,
                    action = gotoUrl(
                        url = urlBuilder.build("settings/2/catalog/add")
                    )
                )
            } else {
                null
            },
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.catalog.count", arrayOf(products.size)),
                            alignment = TextAlignment.Center
                        )
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = ListView(
                            children = products.map {
                                ProductCardWidget.of(
                                    product = it.copy(thumbnailUrl = resize(it.thumbnailUrl)),
                                    country = regulationEngine.country(member.country),
                                    messages = messages,
                                    action = gotoUrl(
                                        url = urlBuilder.build("/settings/2/catalog/product?id=${it.id}")
                                    )
                                )
                            }
                        )
                    )
                )
            )
        ).toWidget()
    }

    private fun resize(url: String?): String? =
        url?.let {
            imageService.transform(
                it,
                Transformation(
                    focus = Focus.AUTO,
                    aspectRatio = AspectRatio(
                        ProductCardWidget.PICTURE_WIDTH.toInt(),
                        ProductCardWidget.PICTURE_HEIGHT.toInt()
                    ),
                    dimension = Dimension(height = ProductCardWidget.PICTURE_HEIGHT.toInt())
                )
            )
        }
}
