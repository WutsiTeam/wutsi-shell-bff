package com.wutsi.application.membership.settings.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitProfileAttributeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class SettingsV2ProfileEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsUrl()}/profile/editor$action?name=$name"

    @Test
    fun `display-name`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-display-name.json", url("display-name"))

    @Test
    fun `email`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-email.json", url("email"))

    @Test
    fun `language`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-language.json", url("language"))

    @Test
    fun `city`() {
        val places = listOf(
            Fixtures.createPlaceSummary(1, "Yaounde"),
            Fixtures.createPlaceSummary(2, "Douala"),
            Fixtures.createPlaceSummary(3, "Bafoussam")
        )
        doReturn(SearchPlaceResponse(places)).whenever(membershipManagerApi).searchPlace(any())
        
        assertEndpointEquals("/membership/settings/profile/screens/editor-city.json", url("city-id"))
    }

    @Test
    fun `timezone`() =
        assertEndpointEquals("/membership/settings/profile/screens/editor-timezone.json", url("timezone-id"))

    @Test
    fun submit() {
        val request = SubmitProfileAttributeRequest(
            value = "Foo"
        )
        val response = rest.postForEntity(url("display-name", "/submit"), request, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "display-name",
                value = request.value
            )
        )
    }
}
