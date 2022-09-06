package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.exception.SmsCodeMismatchException
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/verify-sms-code")
class VerifySmsCodeCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestBody @Valid
        request: VerifySmsCodeRequest
    ): Action {
        service.verifyCode(request)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build(loginUrl, getSubmitUrl())
        )
    }

    @ExceptionHandler(SmsCodeMismatchException::class)
    fun onSmsCodeMismatchException(ex: SmsCodeMismatchException): Action =
        createErrorAction(ex, "page.verify-account-mobile.error.invalid-code")

    private fun getSubmitUrl(): String {
        val me = securityContext.currentAccount()
        return "?phone=" + encodeURLParam(me.phone!!.number) +
            "&screen-id=" + Page.SETTINGS_ACCOUNT_LINK_PIN +
            "&title=" + encodeURLParam(getText("page.settings.account-pin.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.account-pin.sub-title")) +
            "&auth=false" +
            "&return-to-route=false" +
            "&dark-mode=true" +
            "&return-url=" + encodeURLParam(urlBuilder.build("commands/link-mobile-account"))
    }
}
