package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.application.login.endpoint.Page
import com.wutsi.application.login.endpoint.onboard.dto.SavePinRequest
import com.wutsi.application.login.exception.PinMismatchException
import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/confirm-login-pin")
class ConfirmLoginPinCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(
        @Valid @RequestBody
        request: SavePinRequest
    ): Action {
        confirmPin(request)
        return gotoPage(Page.FINAL)
    }

    fun confirmPin(request: SavePinRequest) {
        val state = getState()
        try {
            if (state.pin != request.pin) {
                throw PinMismatchException()
            }
        } finally {
            log(state)
        }
    }
}
