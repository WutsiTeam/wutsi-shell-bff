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
@RequestMapping("/settings/2/business/pages/biography")
class Business05BiographyPage : AbstractBusinessAttributePage() {
    companion object {
        const val PAGE_INDEX = 5
        const val ATTRIBUTE = "biography"
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getAttribute(): String = ATTRIBUTE

    override fun getButton() = Input(
        name = "value",
        type = InputType.Submit,
        caption = getText("page.settings.business.button.next"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsUrl()}/business/pages/biography/submit")
        )
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitBusinessAttributeRequest): Action {
        val entity = dao.get()
        entity.biography = request.value
        dao.save(entity)
        return gotoNextPage()
    }
}
