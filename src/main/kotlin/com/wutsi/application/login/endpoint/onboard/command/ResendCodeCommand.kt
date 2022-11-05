package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/resend-code")
class ResendCodeCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(): Action {
        resendSmsCode()
        return promptInformation("message.info.code-resent")
    }

    private fun resendSmsCode() {
        val state = getState()
        try {
            val state = sendSmsCode(state.phoneNumber)
            log(state)
        } finally {
            log(state)
        }
    }
}
