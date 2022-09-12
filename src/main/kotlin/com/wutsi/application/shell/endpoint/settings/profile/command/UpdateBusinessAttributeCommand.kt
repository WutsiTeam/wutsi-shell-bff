package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/update-business-attribute")
class UpdateBusinessAttributeCommand : AbstractBusinessCommand() {
    @PostMapping
    fun index(
        @RequestParam name: String,
        @RequestParam page: Int,
        @RequestBody request: UpdateAccountAttributeRequest
    ): Action {
        // Get the data
        val key = getKey()
        val data = getData(key)

        // Update the attribute
        if (name == "display-name") {
            data.displayName = request.value!!
        } else if (name == "biography") {
            data.biography = request.value
        } else if (name == "category-id") {
            data.categoryId = request.value?.toLong()
        } else if (name == "city-id") {
            data.cityId = request.value?.toLong()
        } else if (name == "whatsapp") {
            data.whatsapp = request.value
        } else if (name == "street") {
            data.street = request.value
        }

        // Store
        cache.put(key, data)

        // Next
        return Action(
            type = ActionType.Page,
            url = "page:/${page + 1}"
        )
    }
}
