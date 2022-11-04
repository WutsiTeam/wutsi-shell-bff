package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.service.AccountService
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.DialogType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/resend-sms-code")
class ResendSmsCodeCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(): Action {
        val phoneNumber = service.getSmsCodeEntity().phoneNumber
        service.resentVerificationCode()
        return Action(
            type = Prompt,
            prompt = Dialog(
                type = DialogType.Information,
                message = getText("page.verify-account-mobile.message.code-resent", arrayOf(phoneNumber))
            ).toWidget()
        )
    }
}
