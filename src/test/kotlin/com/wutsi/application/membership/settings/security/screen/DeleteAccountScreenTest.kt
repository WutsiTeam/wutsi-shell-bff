package com.wutsi.application.membership.settings.security.screen

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DeleteAccountScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSecurityUrl()}/delete$action"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/security/screens/delete.json", url())

    @Test
    fun submit() {
        // WHEN
        val response = rest.postForEntity(url("/submit"), null, Action::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipManagerApi).deleteMember()

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertTrue(action.url.startsWith("http://localhost:0${Page.getOnboardUrl()}"))
    }
}
