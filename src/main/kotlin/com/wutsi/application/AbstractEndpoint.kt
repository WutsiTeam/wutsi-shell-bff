package com.wutsi.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.login.exception.AuthenticationException
import com.wutsi.application.login.exception.PinMismatchException
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.DialogType.Error
import com.wutsi.platform.core.error.exception.WutsiException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URLEncoder

abstract class AbstractEndpoint {
    @Autowired
    private lateinit var messages: MessageSource

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Value("\${wutsi.application.shell-url}")
    protected lateinit var shellUrl: String

    @ExceptionHandler(Throwable::class)
    fun onException(ex: Throwable) =
        createErrorAction(ex, "message.error.unexpected-error")

    @ExceptionHandler(AuthenticationException::class)
    fun onAuthenticationException(ex: AuthenticationException) =
        createErrorAction(ex, "message.error.login-failed")

    @ExceptionHandler(PinMismatchException::class)
    fun onPinMismatchException(e: PinMismatchException): Action =
        createErrorAction(e, "message.error.pin-mismatch")

    private fun createErrorAction(e: Throwable, messageKey: String): Action {
        val action = Action(
            type = Prompt,
            prompt = Dialog(
                title = getText("prompt.error.title"),
                type = Error,
                message = getText(messageKey)
            ).toWidget()
        )
        log(action, e)
        return action
    }

    protected fun log(action: Action, e: Throwable) {
        log(e)
        logger.add("action_type", action.type)
        logger.add("action_url", action.url)
        logger.add("action_prompt_type", action.prompt?.type)
        logger.add("action_prompt_message", action.prompt?.attributes?.get("message"))
    }

    protected fun log(e: Throwable) {
        logger.setException(e)
        if (e is WutsiException) {
            logger.add("error_code", e.error.code)
        }
    }

    protected fun gotoPage(page: Int) = Action(
        type = ActionType.Page,
        url = "page:/$page"
    )

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale())

    protected fun gotoRoute(path: String, replacement: Boolean? = null, parameters: Map<String, String>? = null) =
        Action(
            type = Route,
            url = "route:$path",
            replacement = replacement,
            parameters = parameters
        )

    protected fun gotoUrl(url: String, type: ActionType = ActionType.Route, replacement: Boolean? = null) = Action(
        type = type,
        url = url,
        replacement = replacement
    )

    protected fun promptError(errorKey: String) = Action(
        type = ActionType.Prompt,
        prompt = Dialog(
            title = getText("prompt.error.title"),
            type = DialogType.Error,
            message = getText(errorKey)
        ).toWidget()
    )

    protected fun formattedPhoneNumber(phoneNumber: String?, country: String? = null): String? {
        if (phoneNumber == null) {
            return null
        }

        val phoneUtil = PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(phoneNumber, country ?: "")
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    protected fun encodeURLParam(text: String?): String =
        text?.let { URLEncoder.encode(it, "utf-8") } ?: ""
}
