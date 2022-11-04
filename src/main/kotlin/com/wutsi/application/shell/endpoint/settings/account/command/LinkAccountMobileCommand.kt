package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.service.AccountService
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/link-mobile-account")
class LinkAccountMobileCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(): Action {
        service.linkMobileAccount()
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/accounts/link/success"),
            replacement = true
        )
    }
}
