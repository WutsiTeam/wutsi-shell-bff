package com.wutsi.application.marketplace.settings.store.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store/pages/start")
class Store00StartPage(
    private val marketplaceManagerApi: MarketplaceManagerApi
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.store.title")

    override fun getSubTitle() = getText("page.settings.store.sub-title")

    override fun getBody(): WidgetAware =
        Column(
            children = listOf(
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.settings.store.button.yes"),
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("${Page.getSettingsStoreUrl()}/pages/start/submit")
                        )
                    )
                ),
                Container(
                    padding = 10.0,
                    child = Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.store.button.no"),
                        action = gotoPreviousScreen()
                    )
                )
            )
        )

    override fun getButton(): WidgetAware? = null

    @PostMapping("/submit")
    fun submit(): Action {
        marketplaceManagerApi.enableStore()
        return gotoPage(PAGE_INDEX + 1)
    }
}
