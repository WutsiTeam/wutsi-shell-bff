package com.wutsi.application.shell.endpoint.settings.profile.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.profile.dto.VerifyOtpRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.VerifyOTPRequest
import com.wutsi.platform.security.util.ErrorURN
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/verify-email")
class VerifyEmailCommand(
    private val securityApi: WutsiSecurityApi,
    private val accountApi: WutsiAccountApi,
    private val objectMapper: ObjectMapper
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestParam email: String,
        @RequestParam token: String,
        @RequestBody request: VerifyOtpRequest
    ): Action {
        try {
            // Validate OTP
            securityApi.verifyOtp(
                token = token,
                request = VerifyOTPRequest(
                    code = request.code
                )
            )

            // Update
            accountApi.updateAccountAttribute(
                id = securityContext.currentAccountId(),
                name = "email",
                request = UpdateAccountAttributeRequest(
                    value = email
                )
            )
            return Action(
                type = ActionType.Route,
                url = "route:/.."
            )
        } catch (ex: FeignException) {
            val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            return if (response.error.code == ErrorURN.MFA_VERIFICATION_FAILED.urn)
                createErrorAction(null, "prompt.error.otp-mismatch")
            else
                createErrorAction(null, "prompt.error.unexpected-error")
        }
    }
}
