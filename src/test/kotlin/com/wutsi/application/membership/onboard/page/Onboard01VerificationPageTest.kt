package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitPhoneRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

internal class Onboard01VerificationPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "fr"
    )

    private val request = SubmitPhoneRequest(
        phoneNumber = PHONE_NUMBER
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/verification$action"

    @BeforeTest
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/verification.json", url())
    }

    @Test
    fun resend() {
        // GIVEN
        doReturn(SearchMemberResponse()).whenever(membershipManagerApi).searchMember(any())

        // GIVEN
        val response = rest.postForEntity(url("/resend"), request, Action::class.java)

        // THEN
        kotlin.test.assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<CreateOTPRequest>()
        com.nhaarman.mockitokotlin2.verify(securityManagerApi).createOtp(req.capture())
        assertEquals(entity.phoneNumber, req.firstValue.address)
        assertEquals(MessagingType.SMS.name, req.firstValue.type)
    }

    @Test
    fun submit() {
        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        kotlin.test.assertEquals(ActionType.Page, action.type)
        kotlin.test.assertEquals("page:/2", action.url)
    }

    @Test
    fun invalidCode() {
        // GIVEN
        val ex = createFeignException("xxx")
        doThrow(ex).whenever(securityManagerApi).verifyOtp(any(), any())

        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertNotNull(action.prompt)
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(getText("message.error.sms-verification-failed"), action.prompt?.attributes?.get("message"))
    }
}
