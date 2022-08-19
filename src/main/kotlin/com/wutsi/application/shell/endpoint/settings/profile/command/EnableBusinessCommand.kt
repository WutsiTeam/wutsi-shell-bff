package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.EnableBusinessRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/enable-business")
class EnableBusinessCommand(
    private val accountApi: WutsiAccountApi,
) : AbstractBusinessCommand() {
    @PostMapping
    fun index(): Action {
        // Get the data
        val account = securityContext.currentAccount()
        val data = getData(getKey())

        // Update the attribute
        accountApi.enableBusiness(
            id = account.id,
            request = EnableBusinessRequest(
                displayName = data.displayName,
                categoryId = data.categoryId,
                biography = data.biography,
                whatsapp = data.whatsapp,
                country = account.country,
                street = data.street,
                cityId = data.cityId
            )
        )

        // Evict account from cache
        securityContext.evictFromCache()

        // Next
        return Action(
            type = ActionType.Route,
            url = "route:/.."
        )
    }
}
