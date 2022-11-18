package com.wutsi.application.membership.settings.profile.screen

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsV2ProfileScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getSettingsUrl()}/profile"

    @Test
    fun personal() = assertEndpointEquals("/membership/settings/profile/screens/personal.json", url())

    @Test
    fun businessNotSupported() {
        val member = Fixtures.createMember(country = "NZ")
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember()

        assertEndpointEquals("/membership/settings/profile/screens/business-not-supported.json", url())
    }
}
