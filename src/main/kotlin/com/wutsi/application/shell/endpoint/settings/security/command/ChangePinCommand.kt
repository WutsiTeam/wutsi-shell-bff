package com.wutsi.application.shell.endpoint.settings.security.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.security.dto.ChangePinRequest
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/change-pin")
class ChangePinCommand(
    private val urlBuilder: URLBuilder,
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: ChangePinRequest): Action =
        Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/security/confirm-pin?pin=${request.pin}"),
            replacement = true
        )
}
