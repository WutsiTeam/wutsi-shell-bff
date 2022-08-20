package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.account.dto.LinkBankAccountRequest
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/link-bank-account")
class LinkAccountBankCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: LinkBankAccountRequest): Action {
        service.linkBankAccount(request)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/accounts/link/success"),
            replacement = true
        )
    }
}
