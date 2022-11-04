package com.wutsi.application.shell.endpoint.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.BankAccount
import com.wutsi.platform.account.dto.GetPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethod
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsAccountDeleteScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/account/delete?token=xxx"
    }

    @Test
    fun index() {
        // GIVEN
        val paymentMethod = PaymentMethod(
            token = "xxxx",
            type = PaymentMethodType.BANK.name,
            provider = PaymentMethodProvider.WAF.name,
            maskedNumber = "...3456",
            bankAccount = BankAccount(
                id = 123,
                number = "123456",
                bankCode = "WAF",
                country = "CM"
            )
        )
        doReturn(GetPaymentMethodResponse(paymentMethod)).whenever(accountApi).getPaymentMethod(any(), any())

        // THEN
        assertEndpointEquals("/shell/screens/settings/accounts/delete.json", url)
    }
}
