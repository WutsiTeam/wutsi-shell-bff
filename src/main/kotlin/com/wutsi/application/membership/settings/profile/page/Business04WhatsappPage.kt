package com.wutsi.application.membership.settings.profile.page

import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitBusinessAttributeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/whatsapp")
class Business04WhatsappPage : AbstractBusinessAttributePage() {
    companion object {
        const val PAGE_INDEX = 4
        const val ATTRIBUTE = "whatsapp"
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getAttribute(): String = ATTRIBUTE

    override fun getButton() = Input(
        name = "value",
        type = InputType.Submit,
        caption = getText("page.settings.business.button.next"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsUrl()}/business/pages/whatsapp/submit")
        )
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitBusinessAttributeRequest): Action {
        val entity = dao.get()
        entity.whatsapp = request.value.toBoolean()
        dao.save(entity)
        return gotoNextPage()
    }
}
