package com.wutsi.application.shell.endpoint.fcm.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.fcm.dto.UpdateTokenRequest
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/update-fcm-token")
class UpdateTokenCommand(
    private val accountApi: WutsiAccountApi
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: UpdateTokenRequest) {
        accountApi.updateAccountAttribute(
            id = securityContext.currentAccountId(),
            name = "fcm-token",
            request = UpdateAccountAttributeRequest(
                value = request.token
            )
        )
    }
}
