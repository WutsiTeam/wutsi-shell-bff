package com.wutsi.application.shell.endpoint

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.BottomNavigationBarWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.DialogType.Error
import com.wutsi.platform.account.dto.PaymentMethod
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.PaymentMethodType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.net.URLEncoder

abstract class AbstractEndpoint {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    private lateinit var phoneNumberUtil: PhoneNumberUtil

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    protected lateinit var securityContext: SecurityContext

    @Value("\${wutsi.application.login-url}")
    protected lateinit var loginUrl: String

    @Value("\${wutsi.application.cash-url}")
    protected lateinit var cashUrl: String

    @Value("\${wutsi.application.store-url}")
    protected lateinit var storeUrl: String

    @Value("\${wutsi.application.news-url}")
    protected lateinit var newsUrl: String

    @Autowired
    protected lateinit var sharedUIMapper: SharedUIMapper

    @Autowired
    protected lateinit var togglesProvider: TogglesProvider

    protected fun bottomNavigationBar() = BottomNavigationBarWidget(
        model = sharedUIMapper.toBottomNavigationBarModel(
            shellUrl = "",
            cashUrl = cashUrl,
            storeUrl = storeUrl,
            newsUrl = if (securityContext.currentAccount().superUser) newsUrl else null,
            urlBuilder = urlBuilder
        )
    ).toBottomNavigationBar()

    protected fun createErrorAction(e: Throwable?, messageKey: String): Action {
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

    private fun log(action: Action, e: Throwable?) {
        logger.add("action_type", action.type)
        logger.add("action_url", action.url)
        logger.add("action_prompt_type", action.prompt?.type)
        logger.add("action_prompt_message", action.prompt?.attributes?.get("message"))
        if (e != null)
            logger.setException(e)

        LoggerFactory.getLogger(this::class.java).error("Unexpected error", e)
    }

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key

    protected fun formattedAccountNumber(paymentMethod: PaymentMethodSummary): String =
        paymentMethod.maskedNumber

    protected fun formattedAccountNumber(paymentMethod: PaymentMethod): String =
        if (paymentMethod.type == PaymentMethodType.MOBILE.name)
            formattedPhoneNumber(paymentMethod.phone.number, paymentMethod.phone.country)
                ?: paymentMethod.maskedNumber
        else
            paymentMethod.maskedNumber

    protected fun formattedPhoneNumber(number: String?, country: String? = null): String? {
        if (number == null)
            return null

        val phoneNumber = phoneNumberUtil.parse(number, country ?: detectCountry(number))
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    private fun detectCountry(number: String): String {
        val phone = phoneNumberUtil.parse(number, "")
        return phoneNumberUtil.getRegionCodeForCountryCode(phone.countryCode)
    }

    protected fun encodeURLParam(text: String?): String =
        text?.let { URLEncoder.encode(it, "utf-8") } ?: ""
}
