package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateProfileAttributeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun update() {
        // GIVEN
        val name = "xxx"
        val request = UpdateAccountAttributeRequest(value = "oreoiroei")

        // WHEN
        val url = "http://localhost:$port/commands/update-profile-attribute?name=$name"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
        assertEquals(false, action.replacement)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, name, UpdateAccountAttributeRequest(request.value))
        verify(cache).evict(any())
    }

    @Test
    fun business() {
        // GIVEN
        val request = UpdateAccountAttributeRequest(value = "true")
        val url = "http://localhost:$port/commands/update-profile-attribute?name=business"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/profile", action.url)
        assertEquals(true, action.replacement)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "business", UpdateAccountAttributeRequest(request.value))
        verify(cache).evict(any())
    }

    @Test
    fun language() {
        // GIVEN
        val request = UpdateAccountAttributeRequest(value = "en")
        val url = "http://localhost:$port/commands/update-profile-attribute?name=language"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
        assertEquals(false, action.replacement)

        assertEquals(true, response.headers["x-language"]?.contains("en"))

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "language", UpdateAccountAttributeRequest(request.value))
        verify(cache).evict(any())
    }

    @Test
    fun enableStore() {
        // GIVEN
        val request = UpdateAccountAttributeRequest(value = "true")
        val url = "http://localhost:$port/commands/update-profile-attribute?name=has-store"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("https://wutsi-store-bff-test.herokuapp.com/settings/store", action.url)
        assertEquals(false, action.replacement)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "has-store", UpdateAccountAttributeRequest(request.value))
        verify(cache).evict(any())
    }
}
