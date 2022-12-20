package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.flutter.sdui.Button
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/success")
class Business07SuccessPage : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 6
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getTitle() = getText("page.settings.business.title")

    override fun getSubTitle() = getText("page.settings.business.congratulations")

    override fun getButton() = Button(
        id = "ok",
        caption = getText("page.settings.business.button.done"),
        action = gotoPreviousScreen(),
    )
}
