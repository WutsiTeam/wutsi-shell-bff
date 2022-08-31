package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.security.dto.CreateOTPRequest
import com.wutsi.platform.security.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class ResendSmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    private lateinit var state: SmsCodeEntity

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/resend-sms-code"

        state = SmsCodeEntity(phoneNumber = "+23799509999", token = "777", carrier = "mtn")
        doReturn(state).whenever(cache).get(any(), eq(SmsCodeEntity::class.java))
    }

    @Test
    fun sendVerification() {
        // GIVEN
        val token = "4309403-4304039"
        doReturn(CreateOTPResponse(token)).whenever(securityApi).createOpt(any())

        // WHEN
        val request = emptyMap<String, String>()
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Information.name, action.prompt?.attributes?.get("type"))

        verify(securityApi).createOpt(CreateOTPRequest(address = state.phoneNumber, type = "SMS"))

        val entity = argumentCaptor<SmsCodeEntity>()
        verify(cache).put(any(), entity.capture())
        assertEquals(state.phoneNumber, entity.firstValue.phoneNumber)
        assertEquals(token, entity.firstValue.token)
    }
}
