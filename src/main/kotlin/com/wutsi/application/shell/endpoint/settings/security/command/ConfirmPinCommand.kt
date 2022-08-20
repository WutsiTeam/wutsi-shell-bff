package com.wutsi.application.shell.endpoint.settings.security.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.security.dto.ChangePinRequest
import com.wutsi.application.shell.exception.PinMismatchException
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/confirm-pin")
class ConfirmPinCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam pin: String, @RequestBody request: ChangePinRequest): Action {
        service.confirmPin(pin, request)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("/settings/security/pin/success"),
            replacement = true
        )
    }

    @ExceptionHandler(PinMismatchException::class)
    fun onPinMismatchException(e: PinMismatchException): Action =
        createErrorAction(e, "prompt.error.pin-mismatch")
}
