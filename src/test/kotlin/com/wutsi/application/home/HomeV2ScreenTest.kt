package com.wutsi.application.home

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.error.ErrorURN
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port${Page.getHomeUrl()}"

    @Test
    fun index() = assertEndpointEquals("/home/screens/index.json", url())

    @Test
    fun `redirect on onboard page if member not found`() {
        val ex = createNotFoundException(errorCode = ErrorURN.MEMBER_NOT_FOUND.urn)
        doThrow(ex).whenever(membershipManagerApi).getMember(any())

        assertEndpointEquals("/membership/onboard/screens/index.json", url())
    }
}
