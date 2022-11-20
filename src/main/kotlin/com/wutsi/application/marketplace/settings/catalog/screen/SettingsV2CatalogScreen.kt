package com.wutsi.application.marketplace.settings.catalog.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.widget.ProductListItemWidget
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
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/catalog")
class SettingsV2CatalogScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine
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
                        url = urlBuilder.build("settings/catalog/add")
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
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = products.map {
                                ProductListItemWidget(
                                    title = it.title,
                                    price = it.price,
                                    country = regulationEngine.country(member.country),
                                    pictureUrl = it.thumbnailUrl,
                                    summary = it.summary,
                                    action = gotoUrl(
                                        url = urlBuilder.build("/settings/product?id=${it.id}")
                                    )
                                )
                            }
                        )
                    )
                )
            )
        ).toWidget()
    }
}
