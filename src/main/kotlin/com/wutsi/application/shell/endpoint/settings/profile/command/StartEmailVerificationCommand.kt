package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.CreateOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/start-email-verification")
class StartEmailVerificationCommand(
    private val securityApi: WutsiSecurityApi
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: UpdateAccountAttributeRequest): Action {
        if (request.value.isNullOrEmpty() || securityContext.currentAccount().email.equals(request.value, true))
            return Action(
                type = ActionType.Route,
                url = "route:/.."
            )

        val response = securityApi.createOpt(
            request = CreateOTPRequest(
                address = request.value ?: "",
                type = MessagingType.EMAIL.name
            )
        )

        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("/settings/profile/email/verification"),
            parameters = mapOf(
                "email" to (request.value ?: ""),
                "token" to response.token
            ),
            replacement = true
        )
    }
}
