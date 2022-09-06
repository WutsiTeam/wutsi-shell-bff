package com.wutsi.application.shell.endpoint.settings.security.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.platform.account.WutsiAccountApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/delete-wallet")
class DeleteWallet(
    private val accountApi: WutsiAccountApi,
    private val logout: LogoutCommand
) : AbstractCommand() {
    @PostMapping
    fun index(): Action {
        accountApi.suspendAccount(securityContext.currentAccountId())
        return logout.index()
    }
}
