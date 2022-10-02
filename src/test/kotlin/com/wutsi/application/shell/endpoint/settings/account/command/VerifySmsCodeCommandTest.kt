package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.security.dto.VerifyOTPRequest
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
internal class VerifySmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @Autowired
    lateinit var messageSource: MessageSource

    private lateinit var state: SmsCodeEntity

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/verify-sms-code"

        state = SmsCodeEntity(phoneNumber = "+23799509999", token = "777", carrier = "mtn")
        doReturn(state).whenever(cache).get(any(), eq(SmsCodeEntity::class.java))
    }

    @Test
    fun index() {
        // GIVEN
        val token = "xxxx"
        doReturn(AddPaymentMethodResponse(token)).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val request = VerifySmsCodeRequest(
            code = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals(
            "https://wutsi-login-bff-test.herokuapp.com/?phone=%2B1237666666666&screen-id=page.settings.account.link.pin&title=Authorisation&sub-title=Enter+your+Wutsi+PIN+to+link+the+account+with+your+Wallet&auth=false&return-to-route=false&dark-mode=true&return-url=http%3A%2F%2Flocalhost%3A0%2Fcommands%2Flink-mobile-account",
            action.url
        )

        verify(securityApi).verifyOtp(state.token, VerifyOTPRequest(request.code))
    }

    @Test
    fun verificationFailed() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(securityApi).verifyOtp(any(), any())

        // WHEN
        val request = VerifySmsCodeRequest(
            code = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(
            messageSource.getMessage(
                "page.verify-account-mobile.error.invalid-code",
                emptyArray(),
                Locale.ENGLISH
            ),
            action.prompt?.attributes?.get("message")
        )
    }
}
