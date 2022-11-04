package com.wutsi.application.store.endpoint.settings.product.profile.screen

import com.wutsi.application.store.endpoint.Page
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.Product
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/store/product/description")
class SettingsProductDescriptionScreen(
    catalogApi: WutsiCatalogApi
) : AbstractSettingsProductAttributeScreen(catalogApi) {
    override fun getAttributeName() = "description"

    override fun getPageId() = Page.SETTINGS_STORE_PRODUCT_DESCRIPTION

    override fun getInputWidget(product: Product): WidgetAware = Input(
        name = "value",
        value = product.description,
        maxLines = 4,
        caption = getText("page.settings.store.product.attribute.${getAttributeName()}")
    )
}
