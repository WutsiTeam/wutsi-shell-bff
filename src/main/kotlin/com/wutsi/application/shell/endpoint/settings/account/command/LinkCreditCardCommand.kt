package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.service.AccountService
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.account.dto.LinkCreditCardRequest
import com.wutsi.application.shell.exception.CreditCardExpiredException
import com.wutsi.application.shell.exception.CreditCardInvalidException
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/link-credit-card")
class LinkCreditCardCommand(
    private val service: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: LinkCreditCardRequest): Action {
        service.linkCreditCard(request)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/accounts/link/success"),
            replacement = true
        )
    }

    @ExceptionHandler(CreditCardInvalidException::class)
    fun onCreditCardInvalidException(ex: CreditCardInvalidException): Action =
        createErrorAction(ex, "prompt.error.credit-card-invalid")

    @ExceptionHandler(CreditCardExpiredException::class)
    fun onCreditCardExpiredException(ex: CreditCardExpiredException): Action =
        createErrorAction(ex, "prompt.error.credit-card-expired")
}
