package com.wutsi.application.shell.endpoint.settings.security.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/delete-wallet")
class DeleteWallet(
    private val accountApi: WutsiAccountApi,
    private val logout: LogoutCommand
) : AbstractCommand() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DeleteWallet::class.java)
    }

    @PostMapping
    fun index(): Action {
        accountApi.suspendAccount(securityContext.currentAccountId())

        try {
            logout.index()
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error while login out")
        } finally {
            return Action(
                type = ActionType.Route,
                url = urlBuilder.build(loginUrl, "/onboard")
            )
        }
    }
}
