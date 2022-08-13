package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.exception.AccountAlreadyLinkedException
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.payment.PaymentMethodType.MOBILE
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/link-mobile-account")
class LinkMobileAccountCommand(
    private val service: AccountService,
) : AbstractCommand() {
    @PostMapping
    fun index(): Action {
        service.linkMobileAccount(MOBILE)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/accounts/link/success")
        )
    }

    @ExceptionHandler(AccountAlreadyLinkedException::class)
    fun onAccountAlreadyLinkedException(ex: AccountAlreadyLinkedException): Action =
        createErrorAction(ex, "page.verify-account-mobile.error.already-linked")
}
