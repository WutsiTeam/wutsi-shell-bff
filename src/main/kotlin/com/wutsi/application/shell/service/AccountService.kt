package com.wutsi.application.shell.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.settings.account.dto.SendSmsCodeRequest
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.endpoint.settings.security.dto.ChangePinRequest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.application.shell.exception.AccountAlreadyLinkedException
import com.wutsi.application.shell.exception.InvalidPhoneNumberException
import com.wutsi.application.shell.exception.PinMismatchException
import com.wutsi.application.shell.exception.SmsCodeMismatchException
import com.wutsi.application.shell.exception.toErrorResponse
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.sms.WutsiSmsApi
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.Tenant
import feign.FeignException
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import org.springframework.web.servlet.LocaleResolver
import javax.servlet.http.HttpServletRequest

@Service
class AccountService(
    private val tenantProvider: TenantProvider,
    private val smsApi: WutsiSmsApi,
    private val accountApi: WutsiAccountApi,
    private val deviceIdProvider: DeviceIdProvider,
    private val httpServletRequest: HttpServletRequest,
    private val localeResolver: LocaleResolver,
    private val securityContext: SecurityContext,
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
    private val cache: Cache
) {
    fun sendVerificationCode(request: SendSmsCodeRequest) {
        logger.add("phone_number", request.phoneNumber)

        val tenant = tenantProvider.get()
        val carrier = findCarrier(request.phoneNumber, tenant)
            ?: throw InvalidPhoneNumberException()

        val verificationId = sendVerificationCode(request.phoneNumber)
        logger.add("verification_id", verificationId)
        storeVerificationNumber(request.phoneNumber, verificationId, carrier.code)
    }

    fun resentVerificationCode() {
        val state = getSmsCodeEntity()
        log(state)

        val verificationId = sendVerificationCode(state.phoneNumber)
        logger.add("verification_id", verificationId)
        storeVerificationNumber(state.phoneNumber, verificationId, state.carrier)
    }

    fun verifyCode(request: VerifySmsCodeRequest) {
        val state = getSmsCodeEntity()
        log(state)
        logger.add("verification_code", request.code)

        try {
            smsApi.validateVerification(
                id = state.verificationId,
                code = request.code
            )
        } catch (ex: Exception) {
            throw SmsCodeMismatchException(ex)
        }
    }

    fun linkMobileAccount(type: PaymentMethodType) {
        try {
            val state = getSmsCodeEntity()
            log(state)

            val principal = securityContext.principal()
            val response = accountApi.addPaymentMethod(
                principal.id.toLong(),
                request = AddPaymentMethodRequest(
                    ownerName = principal.name,
                    number = state.phoneNumber,
                    type = type.name,
                    provider = toPaymentProvider(state.carrier)!!.name
                )
            )
            logger.add("payment_method_token", response.token)
        } catch (ex: FeignException) {
            val code = ex.toErrorResponse(objectMapper)?.error?.code ?: throw ex
            if (code == com.wutsi.platform.account.error.ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn)
                throw AccountAlreadyLinkedException(ex)
            else
                throw ex
        }
    }

    fun getPaymentMethods(tenant: Tenant): List<PaymentMethodSummary> {
        val userId = currentUserId()
        return accountApi.listPaymentMethods(userId).paymentMethods
            .filter { findMobileCarrier(tenant, it) != null }
    }

    fun getLogoUrl(tenant: Tenant, paymentMethod: PaymentMethodSummary): String? {
        val carrier = findMobileCarrier(tenant, paymentMethod)
        if (carrier != null) {
            return tenantProvider.logo(carrier)
        }
        return null
    }

    fun confirmPin(pin: String, request: ChangePinRequest) {
        if (pin != request.pin)
            throw PinMismatchException()

        accountApi.savePassword(
            id = currentUserId(),
            request = SavePasswordRequest(
                password = request.pin
            )
        )
    }

    private fun log(state: SmsCodeEntity) {
        logger.add("phone_carrier", state.carrier)
        logger.add("phone_number", state.phoneNumber)
        logger.add("verification_id", state.verificationId)
    }

    private fun findMobileCarrier(tenant: Tenant, paymentMethod: PaymentMethodSummary): MobileCarrier? =
        tenant.mobileCarriers.find { it.code.equals(paymentMethod.provider, true) }

    fun getSmsCodeEntity(): SmsCodeEntity =
        cacheKey().let {
            cache.get(it, SmsCodeEntity::class.java)
                ?: throw NotFoundException(
                    error = Error(
                        code = "phone-not-found",
                        data = mapOf(
                            "cache-key" to it
                        )
                    )
                )
        }

    private fun storeVerificationNumber(phoneNumber: String, verificationId: Long, carrier: String) {
        cache.put(
            cacheKey(),
            SmsCodeEntity(
                phoneNumber = phoneNumber,
                carrier = carrier,
                verificationId = verificationId
            )
        )
    }

    private fun toPaymentProvider(carrier: String): PaymentMethodProvider? =
        PaymentMethodProvider.values().find { it.name.equals(carrier, ignoreCase = true) }

    private fun sendVerificationCode(phoneNumber: String): Long =
        smsApi.sendVerification(
            SendVerificationRequest(
                phoneNumber = phoneNumber,
                language = localeResolver.resolveLocale(httpServletRequest).language
            )
        ).id

    private fun findCarrier(phoneNumber: String, tenant: Tenant): MobileCarrier? {
        val carriers = tenantProvider.mobileCarriers(tenant)
        return carriers.find { hasPrefix(phoneNumber, it) }
    }

    private fun hasPrefix(phoneNumber: String, carrier: MobileCarrier): Boolean =
        carrier.phonePrefixes.flatMap { it.prefixes }
            .find { phoneNumber.startsWith(it) } != null

    private fun cacheKey(): String =
        "verification-code-" + deviceIdProvider.get(httpServletRequest)

    private fun currentUserId(): Long =
        securityContext.currentAccountId()
}
