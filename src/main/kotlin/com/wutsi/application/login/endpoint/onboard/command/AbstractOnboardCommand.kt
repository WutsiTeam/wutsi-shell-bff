package com.wutsi.application.login.endpoint.onboard.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.login.endpoint.AbstractCommand
import com.wutsi.application.login.entity.AccountEntity
import com.wutsi.application.login.exception.PhoneAlreadyAssignedException
import com.wutsi.application.service.OnboardService
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.CreateOTPRequest
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractOnboardCommand : AbstractCommand() {
    companion object {
        // See https://en.wikipedia.org/wiki/Telephone_numbers_in_Canada
        private val CANADA_PREFIX: List<String> = listOf(
            403, 587, 780, 825,
            236, 250, 604, 672, 778,
            204, 431,
            506,
            709,
            782, 902,
            226, 249, 289, 343, 365, 416, 437, 519, 548, 613, 647, 705, 807, 905,
            367, 418, 438, 450, 514, 579, 581, 819, 873,
            306, 639,
            867
        ).map { "+1$it" }
    }

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    private lateinit var tracingContext: TracingContext

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var cache: Cache

    @Autowired
    protected lateinit var securityApi: WutsiSecurityApi

    @Autowired
    private lateinit var accountApi: WutsiAccountApi

    @Autowired
    private lateinit var phoneNumberUtil: PhoneNumberUtil

    @ExceptionHandler(PhoneAlreadyAssignedException::class)
    fun onPhoneAlreadyAssignedException(e: PhoneAlreadyAssignedException): Action {
        logger.add("phone_already_assigned", "true")

        val state = getState()
        return gotoUrl(
            url = urlBuilder.build(
                "/login?title=" + encodeURLParam(getText("page.login.title")) +
                    "&sub-title=" + encodeURLParam(getText("page.login.sub-title")) +
                    "&phone=" + encodeURLParam(state.phoneNumber) +
                    "&return-to-route=true" +
                    "&return-url=" + encodeURLParam("route:/") +
                    "&hide-change-account-button=true"
            ),
            type = ActionType.Route,
            replacement = true
        )
    }

    @ExceptionHandler(NotFoundException::class)
    fun onNotFoundException(e: NotFoundException): ResponseEntity<Action> {
        if (e.error.code == OnboardService.DEVICE_NOT_FOUND) {
            val action = gotoPage(com.wutsi.application.login.endpoint.Page.PHONE)
            log(action, e)
            return ResponseEntity.ok(action)
        } else {
            log(e)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    protected fun save(state: AccountEntity): AccountEntity {
        cache.put(tracingContext.deviceId(), state)
        return state
    }

    protected fun sendSmsCode(phoneNumber: String): AccountEntity {
        val country = detect(phoneNumber)
        val language = LocaleContextHolder.getLocale().language

        // Send verification
        val token = securityApi.createOpt(
            request = CreateOTPRequest(
                address = phoneNumber,
                type = MessagingType.SMS.name
            )
        ).token

        // Update state
        return save(
            AccountEntity(
                deviceId = tracingContext.deviceId(),
                phoneNumber = phoneNumber,
                country = country,
                language = language,
                otpToken = token
            )
        )
    }

    protected fun log(state: AccountEntity) {
        logger.add("phone_number", state.phoneNumber)
        logger.add("display_name", state.displayName)
        logger.add("country", state.country)
        logger.add("account_id", state.accountId)
        logger.add("city_id", state.cityId)
        logger.add("language", state.language)
        logger.add("payment_phone_number", state.paymentPhoneNumber)
        logger.add("otp_token", state.otpToken)
        logger.add("verification_id", state.pin?.let { "***" })
    }

    protected fun findAccount(state: AccountEntity): AccountSummary? {
        val accounts = accountApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = state.phoneNumber,
                status = AccountStatus.ACTIVE.name
            )
        ).accounts
        return if (accounts.isNotEmpty()) {
            accounts[0]
        } else {
            null
        }
    }

    protected fun createAccount(state: AccountEntity): Long =
        accountApi.createAccount(
            CreateAccountRequest(
                phoneNumber = state.phoneNumber,
                displayName = state.displayName,
                language = state.language,
                country = state.country,
                password = state.pin,
                addPaymentMethod = true,
                cityId = state.cityId
            )
        ).id

    protected fun toErrorResponse(ex: FeignException): ErrorResponse {
        val buff = ex.responseBody().get()
        val bytes = ByteArray(buff.remaining())
        buff.get(bytes)
        return mapper.readValue(bytes, ErrorResponse::class.java)
    }

    fun detect(phoneNumber: String): String {
        val phone = phoneNumberUtil.parse(phoneNumber, "")
        val country = phoneNumberUtil.getRegionCodeForCountryCode(phone.countryCode)
        if (country == "US") {
            if (isFromCanada(phoneNumber)) {
                return "CA"
            }
        }
        return country
    }

    private fun isFromCanada(phoneNumber: String) =
        CANADA_PREFIX.contains(phoneNumber.substring(0, 5))
}
