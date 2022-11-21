package com.wutsi.application.marketplace.settings.catalog.add.page

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.marketplace.settings.catalog.add.dao.PictureRepository
import com.wutsi.application.marketplace.settings.catalog.add.dto.SubmitProductRequest
import com.wutsi.application.widget.PictureWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/catalog/add/pages/editor")
class AppProduct01EditorPage(
    private val dao: PictureRepository,
    private val membershipManagerApi: MembershipManagerApi,
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.catalog.add.product.title")

    override fun getBody(): WidgetAware {
        val member = membershipManagerApi.getMember().member
        val country = regulationEngine.country(member.country)
        val hasNoDecimal = country.monetaryFormat.indexOf(".") == -1
        val picture = dao.get()

        return Column(
            children = listOfNotNull(
                picture.url?.let {
                    PictureWidget(url = it, width = 100.0, height = 100.0)
                },
                Container(
                    padding = 10.0,
                    child = Input(
                        name = "title",
                        maxLength = 100,
                        caption = getText("page.settings.catalog.add.product.attribute.title"),
                        required = true
                    )
                ),
                Container(
                    padding = 10.0,
                    child = Input(
                        name = "price",
                        maxLength = 10,
                        caption = getText("page.settings.catalog.add.product.attribute.price"),
                        type = InputType.Number,
                        suffix = country.currencySymbol,
                        required = true,
                        inputFormatterRegex = if (hasNoDecimal)
                            "[0-9]"
                        else
                            null
                    )
                ),
                Container(
                    padding = 10.0,
                    child = Input(
                        name = "quantity",
                        caption = getText("page.settings.catalog.add.product.attribute.quantity"),
                        type = InputType.Number,
                        required = true,
                        inputFormatterRegex = "[0-9]"
                    )
                )
            )
        )
    }

    override fun getButton() = Input(
        name = "submit",
        type = InputType.Submit,
        caption = getText("page.settings.catalog.add.button.save"),
        action = executeCommand(
            url = urlBuilder.build("/settings/2/catalog/add/pages/editor/submit")
        )
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitProductRequest): Action {
        val picture = dao.get()
        marketplaceManagerApi.createProduct(
            request = CreateProductRequest(
                pictureUrl = picture.url,
                title = request.title,
                summary = request.summary,
                quantity = if (request.quantity.isNullOrEmpty()) null else request.quantity.toInt(),
                price = request.price
            )
        )

        return gotoPreviousScreen()
    }
}
