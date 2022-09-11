package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.profile.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.security.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateBusinessAttributeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun displayName() {
        // WHEN
        val value = "John Smith"
        val response = post("display-name", value, 1)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 2)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value, data.firstValue.displayName)
    }

    @Test
    fun biographie() {
        // WHEN
        val value = "John Smith"
        val response = post("biography", value, 2)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 3)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value, data.firstValue.biography)
    }

    @Test
    fun category() {
        // WHEN
        val value = "1111"
        val response = post("category-id", value, 3)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 4)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value.toLong(), data.firstValue.categoryId)
    }

    @Test
    fun city() {
        // WHEN
        val value = "1111"
        val response = post("city-id", value, 3)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 4)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value.toLong(), data.firstValue.cityId)
    }

    @Test
    fun street() {
        // WHEN
        val value = "3030 linton"
        val response = post("street", value, 4)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 5)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value, data.firstValue.street)
    }

    @Test
    fun whatsapp() {
        // WHEN
        val value = "+151454095049"
        val response = post("whatsapp", value, 5)

        val token = "xxx"
        doReturn(CreateOTPResponse(token = token)).whenever(securityApi).createOpt(any())

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertPage(response, 6)

        val data = argumentCaptor<BusinessEntity>()
        verify(cache).put(any(), data.capture())
        assertEquals(value, data.firstValue.whatsapp)
//        assertEquals(token, data.firstValue.otpToken)
    }

    private fun post(name: String, value: String, page: Int): ResponseEntity<Action> {
        val url = "http://localhost:$port//commands/update-business-attribute?name=$name&page=$page"
        val request = UpdateAccountAttributeRequest(value = value)
        return rest.postForEntity(url, request, Action::class.java)
    }

    private fun assertPage(response: ResponseEntity<Action>, page: Int) {
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/$page", action.url)
    }
}
