package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.endpoint.settings.account.dto.LinkBankAccountRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.payment.PaymentMethodProvider
import feign.FeignException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.MessageSource
import java.util.Locale
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class LinkAccountBankCommandTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @Autowired
    lateinit var messageSource: MessageSource

    val request = LinkBankAccountRequest(
        ownerName = "RAY SPONSIBLE",
        bankCode = "WAF",
        number = "120932093",
        country = "CM"
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/link-bank-account"
    }

    @Test
    fun index() {
        // GIVEN
        val token = "xxxx"
        doReturn(AddPaymentMethodResponse(token)).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/accounts/link/success", action.url)
        assertEquals(true, action.replacement)

        val entity = argumentCaptor<AddPaymentMethodRequest>()
        verify(accountApi).addPaymentMethod(eq(ACCOUNT_ID), entity.capture())
        assertEquals(request.bankCode, entity.firstValue.bankCode)
        assertEquals(request.number, entity.firstValue.number)
        assertEquals(request.ownerName, entity.firstValue.ownerName)
        assertEquals(PaymentMethodProvider.WAF.name, entity.firstValue.provider)
        assertEquals(request.country, entity.firstValue.country)
    }

    @Test
    fun accountAlreadyLinked() {
        // GIVEN
        val ex = toFeignException(ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn)
        doThrow(ex).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(
            messageSource.getMessage(
                "page.verify-account-mobile.error.already-linked",
                emptyArray(),
                Locale.ENGLISH
            ),
            action.prompt?.attributes?.get("message")
        )
    }

    private fun toFeignException(code: String): FeignException {
        val ex: FeignException = mock()
        val content = """
        {
            "error":{
                "code": "$code"
            }
        }
        """
        doReturn(content).whenever(ex).contentUTF8()
        return ex
    }
}
