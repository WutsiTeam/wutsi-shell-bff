package com.wutsi.application.shell.endpoint.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
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
internal class SettingsAccountProfileScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    private val token = "xxxx"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/account/profile?token=$token"
    }

    @Test
    fun `bank account`() {
        // GIVEN
        val paymentMethod = PaymentMethod(
            token = token,
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
        assertEndpointEquals("/shell/screens/settings/accounts/profile/bank.json", url)
    }
}
