package com.wutsi.application.shell.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.settings.account.dto.LinkBankAccountRequest
import com.wutsi.application.shell.endpoint.settings.account.dto.LinkCreditCardRequest
import com.wutsi.application.shell.endpoint.settings.account.dto.SendSmsCodeRequest
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.endpoint.settings.security.dto.ChangePinRequest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.application.shell.exception.AccountAlreadyLinkedException
import com.wutsi.application.shell.exception.CreditCardExpiredException
import com.wutsi.application.shell.exception.CreditCardInvalidException
import com.wutsi.application.shell.exception.InvalidPhoneNumberException
import com.wutsi.application.shell.exception.PinMismatchException
import com.wutsi.application.shell.exception.SmsCodeMismatchException
import com.wutsi.application.shell.exception.toErrorResponse
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.PaymentMethod
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.messaging.MessagingType
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.CreateOTPRequest
import com.wutsi.platform.security.dto.VerifyOTPRequest
import com.wutsi.platform.tenant.dto.CreditCardType
import com.wutsi.platform.tenant.dto.FinancialInstitution
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.Tenant
import feign.FeignException
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class AccountService(
    private val tenantProvider: TenantProvider,
    private val accountApi: WutsiAccountApi,
    private val securityApi: WutsiSecurityApi,
    private val deviceIdProvider: DeviceIdProvider,
    private val httpServletRequest: HttpServletRequest,
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
            securityApi.verifyOtp(
                token = state.token,
                request = VerifyOTPRequest(
                    code = request.code
                )
            )
        } catch (ex: Exception) {
            throw SmsCodeMismatchException(ex)
        }
    }

    fun linkMobileAccount() {
        val state = getSmsCodeEntity()
        log(state)

        val principal = securityContext.principal()
        linkAccount(
            request = AddPaymentMethodRequest(
                ownerName = principal.name,
                number = state.phoneNumber,
                provider = toPaymentProvider(state.carrier)!!.name
            )
        )
    }

    fun linkBankAccount(request: LinkBankAccountRequest) {
        linkAccount(
            request = AddPaymentMethodRequest(
                number = request.number,
                bankCode = request.bankCode,
                ownerName = request.ownerName,
                provider = toPaymentProvider(request.bankCode)!!.name,
                country = request.country
            )
        )
    }

    fun linkCreditCard(request: LinkCreditCardRequest) {
        val tenant = tenantProvider.get()
        linkAccount(
            request = AddPaymentMethodRequest(
                number = request.number,
                expiryMonth = request.expiryMonth,
                expiryYear = request.expiryYear,
                provider = getCreditCardProvider(request.number, tenant).name,
                ownerName = request.ownerName
            )
        )
    }

    /**
     * See https://howtodoinjava.com/java/regex/java-regex-validate-credit-card-numbers/
     */
    private fun getCreditCardProvider(number: String, tenant: Tenant): PaymentMethodProvider =
        if (number.startsWith("4") && (number.length == 13 || number.length == 16)) {
            PaymentMethodProvider.VISA
        } else if (number.substring(0, 2).toInt() in 51..55) {
            PaymentMethodProvider.MASTERCARD
        } else {
            throw CreditCardInvalidException()
        }

    fun linkAccount(request: AddPaymentMethodRequest) {
        try {
            accountApi.addPaymentMethod(
                id = securityContext.currentAccountId(),
                request = request
            )
        } catch (ex: FeignException) {
            val code = ex.toErrorResponse(objectMapper)?.error?.code ?: throw ex
            if (code == com.wutsi.platform.account.error.ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn)
                throw AccountAlreadyLinkedException(ex)
            else if (code == com.wutsi.platform.account.error.ErrorURN.CREDIT_CARD_NUMBER_EXPIRED.urn)
                throw CreditCardExpiredException()
            else if (code == com.wutsi.platform.account.error.ErrorURN.CREDIT_CARD_NUMBER_MALFORMED.urn)
                throw CreditCardInvalidException()
            else
                throw ex
        }
    }

    fun getPaymentMethods(tenant: Tenant): List<PaymentMethodSummary> {
        val userId = currentUserId()
        return accountApi.listPaymentMethods(userId).paymentMethods
    }

    fun getLogoUrl(tenant: Tenant, paymentMethod: PaymentMethodSummary): String? {
        if (paymentMethod.type == PaymentMethodType.MOBILE.name) {
            val carrier = findMobileCarrier(tenant, paymentMethod.provider)
            if (carrier != null) {
                return tenantProvider.logo(carrier)
            }
        } else if (paymentMethod.type == PaymentMethodType.BANK.name) {
            val financialInstitution = findFinancialInstitution(tenant, paymentMethod.provider)
            if (financialInstitution != null) {
                return tenantProvider.logo(financialInstitution)
            }
        } else if (paymentMethod.type == PaymentMethodType.CREDIT_CARD.name) {
            val creditCard = findCreditCardType(tenant, paymentMethod.provider)
            if (creditCard != null) {
                return tenantProvider.logo(creditCard)
            }
        }
        return null
    }

    fun getLogoUrl(tenant: Tenant, paymentMethod: PaymentMethod): String? {
        if (paymentMethod.type == PaymentMethodType.MOBILE.name) {
            val carrier = findMobileCarrier(tenant, paymentMethod.provider)
            if (carrier != null) {
                return tenantProvider.logo(carrier)
            }
        } else if (paymentMethod.type == PaymentMethodType.BANK.name) {
            val financialInstitution = findFinancialInstitution(tenant, paymentMethod.provider)
            if (financialInstitution != null) {
                return tenantProvider.logo(financialInstitution)
            }
        } else if (paymentMethod.type == PaymentMethodType.CREDIT_CARD.name) {
            val creditCardType = findCreditCardType(tenant, paymentMethod.provider)
            if (creditCardType != null) {
                return tenantProvider.logo(creditCardType)
            }
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
        logger.add("token", state.token)
    }

    fun findMobileCarrier(tenant: Tenant, provider: String): MobileCarrier? =
        tenant.mobileCarriers.find { it.code.equals(provider, true) }

    fun findFinancialInstitution(tenant: Tenant, provider: String): FinancialInstitution? =
        tenant.financialInstitutions.find { it.code.equals(provider, true) }

    fun findCreditCardType(tenant: Tenant, provider: String): CreditCardType? =
        tenant.creditCardTypes.find { it.code.equals(provider, true) }

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

    private fun storeVerificationNumber(phoneNumber: String, token: String, carrier: String) {
        cache.put(
            cacheKey(),
            SmsCodeEntity(
                phoneNumber = phoneNumber,
                carrier = carrier,
                token = token
            )
        )
    }

    private fun toPaymentProvider(carrier: String): PaymentMethodProvider? =
        PaymentMethodProvider.values().find { it.name.equals(carrier, ignoreCase = true) }

    private fun sendVerificationCode(phoneNumber: String): String =
        securityApi.createOpt(
            request = CreateOTPRequest(
                address = phoneNumber,
                type = MessagingType.SMS.name
            )
        ).token

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
