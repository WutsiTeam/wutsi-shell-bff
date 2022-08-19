package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.account.dto.SendSmsCodeRequest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.sms.WutsiSmsApi
import com.wutsi.platform.sms.dto.SendVerificationResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class SendSmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    lateinit var smsApi: WutsiSmsApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/send-sms-code"
    }

    @Test
    fun sendVerification() {
        // GIVEN
        val verificationId = 777L
        doReturn(SendVerificationResponse(verificationId)).whenever(smsApi).sendVerification(any())

        // WHEN
        val request = SendSmsCodeRequest(
            phoneNumber = PHONE_NUMBER
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/accounts/verify/mobile", action.url)

        val entity = argumentCaptor<SmsCodeEntity>()
        verify(cache).put(eq("verification-code-$DEVICE_ID"), entity.capture())
        assertEquals(request.phoneNumber, entity.firstValue.phoneNumber)
        assertEquals(verificationId, entity.firstValue.verificationId)
        assertEquals("mtn", entity.firstValue.carrier)
    }

    @Test
    fun invalidPhoneNumber() {
        // WHEN
        val request = SendSmsCodeRequest(
            phoneNumber = "111111111"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))

        verify(cache, never()).put(eq("verification-code-$DEVICE_ID"), any())
    }
}
