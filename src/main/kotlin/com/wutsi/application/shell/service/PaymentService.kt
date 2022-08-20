package com.wutsi.application.shell.service

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentApi: WutsiPaymentApi,
    private val securityContext: SecurityContext
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentService::class.java)
    }

    fun getBalance(tenant: Tenant): Money {
        try {
            val userId = securityContext.currentAccountId()
            val balance = paymentApi.getBalance(userId).balance
            return Money(
                value = balance.amount,
                currency = balance.currency
            )
        } catch (ex: Throwable) {
            LOGGER.error("Unexpected error while resolving the balance", ex)
            return Money(currency = tenant.currency)
        }
    }
}
