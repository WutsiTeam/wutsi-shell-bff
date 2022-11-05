package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.application.login.endpoint.Page
import com.wutsi.application.login.endpoint.onboard.dto.VerifySmsCodeRequest
import com.wutsi.application.login.exception.PhoneAlreadyAssignedException
import com.wutsi.flutter.sdui.Action
import com.wutsi.platform.security.dto.VerifyOTPRequest
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("commands/verify-code")
class VerifyCodeCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(
        @Valid @RequestBody
        request: VerifySmsCodeRequest
    ): Action {
        try {
            verifyCode(request)
            return gotoPage(Page.PROFILE)
        } catch (e: FeignException.Conflict) {
            return promptError("message.error.sms-verification-failed")
        }
    }

    fun verifyCode(request: VerifySmsCodeRequest) {
        val state = getState()
        try {
            securityApi.verifyOtp(
                token = state.otpToken,
                request = VerifyOTPRequest(code = request.code)
            )

            if (findAccount(state) != null) {
                throw PhoneAlreadyAssignedException(state.phoneNumber)
            }
        } finally {
            log(state)
        }
    }
}
