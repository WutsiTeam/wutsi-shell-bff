package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.application.login.endpoint.Page
import com.wutsi.application.login.endpoint.onboard.dto.SavePinRequest
import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/save-pin")
class SavePinCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(
        @Valid @RequestBody
        request: SavePinRequest
    ): Action {
        savePin(request)
        return gotoPage(Page.CONFIRM_PIN)
    }

    private fun savePin(request: SavePinRequest) {
        val state = getState()
        try {
            state.pin = request.pin
            save(state)
        } finally {
            log(state)
        }
    }
}
