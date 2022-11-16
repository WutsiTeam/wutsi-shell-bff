package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class Onboard06SuccessPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "fr",
        displayName = "Ray Sponsible",
        pin = "123456"
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/success$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/success.json", url())
    }

    @Test
    fun start() {
        // GIVEN
        val response = rest.postForEntity(url("/start"), null, Action::class.java)

        // THEN
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals(
            "http://localhost:0/login?title=&sub-title=Enter+your+Password&phone=%2B237670000010&return-to-route=true&return-url=route%3A%2F&hide-change-account-button=true",
            action.url
        )
    }
}
