package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/delete-account")
class DeleteAccountCommand(
    private val accountApi: WutsiAccountApi
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam token: String): Action {
        accountApi.deactivatePaymentMethod(securityContext.currentAccountId(), token)
        return Action(
            type = ActionType.Route,
            url = "route:/.."
        )
    }
}
