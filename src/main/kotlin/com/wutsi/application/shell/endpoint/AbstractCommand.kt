package com.wutsi.application.shell.endpoint

import com.wutsi.application.shell.exception.AccountAlreadyLinkedException
import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractCommand : AbstractEndpoint() {
    @ExceptionHandler(AccountAlreadyLinkedException::class)
    fun onAccountAlreadyLinkedException(ex: AccountAlreadyLinkedException): Action =
        createErrorAction(ex, "page.verify-account-mobile.error.already-linked")

    @ExceptionHandler(Throwable::class)
    fun onThrowable(ex: Throwable): Action =
        createErrorAction(ex, "prompt.error.unexpected-error")
}
