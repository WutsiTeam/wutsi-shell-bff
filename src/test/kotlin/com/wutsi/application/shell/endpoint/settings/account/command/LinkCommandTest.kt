package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import feign.FeignException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.MessageSource
import org.springframework.test.context.ActiveProfiles
import java.util.Locale

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class LinkCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @Autowired
    lateinit var messageSource: MessageSource

    private lateinit var state: SmsCodeEntity

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/link-account"

        state = SmsCodeEntity(phoneNumber = "+23799509999", verificationId = 777L, carrier = "mtn")
        doReturn(state).whenever(cache).get(any(), eq(SmsCodeEntity::class.java))
    }

    @Test
    fun index() {
        // GIVEN
        val token = "xxxx"
        doReturn(AddPaymentMethodResponse(token)).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val response = rest.postForEntity(url, null, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/accounts/link/success", action.url)

        val entity = argumentCaptor<AddPaymentMethodRequest>()
        verify(accountApi).addPaymentMethod(eq(ACCOUNT_ID), entity.capture())
        assertEquals(state.phoneNumber, entity.firstValue.number)
        assertEquals(PaymentMethodType.MOBILE.name, entity.firstValue.type)
        assertEquals(PaymentMethodProvider.MTN.name, entity.firstValue.provider)
        assertEquals(ACCOUNT_NAME, entity.firstValue.ownerName)
    }

    @Test
    fun accountAlreadyLinked() {
        // GIVEN
        val ex = toFeignException(ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn)
        doThrow(ex).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val response = rest.postForEntity(url, null, Action::class.java)

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
