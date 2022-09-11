package com.wutsi.application.shell.endpoint.settings.profile.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shell.endpoint.settings.profile.dto.VerifyOtpRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.VerifyOTPRequest
import com.wutsi.platform.security.util.ErrorURN
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/verify-business-whatsapp")
class VerifyWhatsappCommand(
    private val securityApi: WutsiSecurityApi,
    private val objectMapper: ObjectMapper
) : AbstractBusinessCommand() {
    @PostMapping
    fun index(
        @RequestBody request: VerifyOtpRequest
    ): Action {
        val key = getKey()
        val data = getData(key)

        try {
            // Validate OTP
            securityApi.verifyOtp(
                token = data.otpToken ?: "",
                request = VerifyOTPRequest(
                    code = request.code
                )
            )

            // Update
            return Action(
                type = ActionType.Page,
                url = "page:/8"
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
