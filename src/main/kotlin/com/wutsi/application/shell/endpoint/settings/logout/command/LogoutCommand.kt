package com.wutsi.application.shell.endpoint.settings.logout.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.LogoutRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/logout")
class LogoutCommand(
    private val securityApi: WutsiSecurityApi,
    private val tokenProvider: TokenProvider
) : AbstractCommand() {
    @PostMapping
    fun execute(): Action {
        tokenProvider.getToken()?.let {
            securityApi.logout(
                request = LogoutRequest(accessToken = it)
            )
        }
        return Action(
            type = ActionType.Route,
            url = "route:/~",
            replacement = true
        )
    }
}
