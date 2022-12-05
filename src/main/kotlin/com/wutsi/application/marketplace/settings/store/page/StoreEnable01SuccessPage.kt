package com.wutsi.application.marketplace.settings.store.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.flutter.sdui.Button
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store/enable/pages/success")
class StoreEnable01SuccessPage : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 6
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getTitle() = getText("page.settings.store.enable.title")

    override fun getSubTitle() = getText("page.settings.store.enable.sub-title")

    override fun getButton() = Button(
        caption = getText("page.settings.store.enable.button.done"),
        action = gotoUrl(
            url = urlBuilder.build(Page.getSettingsProductListUrl()),
            replacement = true
        )
    )
}
