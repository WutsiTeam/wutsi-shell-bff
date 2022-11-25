package com.wutsi.application.marketplace.settings.catalog.product.screen

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.marketplace.service.ProductEditorWidgetProvider
import com.wutsi.application.marketplace.settings.catalog.add.dto.SubmitProductAttributeRequest
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/catalog/product/editor")
class SettingsV2ProductAttributeScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val widgetProvider: ProductEditorWidgetProvider
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long, @RequestParam name: String): Widget {
        val product = marketplaceManagerApi.getProduct(id).product
        return Screen(
            id = Page.SETTINGS_CATALOG_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.product.attribute.$name")
            ),
            child = Form(
                children = listOfNotNull(
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(getText("page.settings.catalog.product.attribute.$name.description"))
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Container(
                        padding = 10.0,
                        child = widgetProvider.get(name, product)
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.store.product.attribute.button.submit"),
                            action = executeCommand(
                                url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor/submit"),
                                parameters = mapOf(
                                    "id" to id.toString(),
                                    "name" to name
                                )
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestBody request: SubmitProductAttributeRequest
    ): Action {
        marketplaceManagerApi.updateProductAttribute(
            request = UpdateProductAttributeListRequest(
                productId = id,
                attributes = listOf(
                    ProductAttribute(
                        name = name,
                        value = request.value
                    )
                )
            )
        )

        return gotoPreviousScreen()
    }
}
