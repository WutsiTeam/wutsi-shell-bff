package com.wutsi.application.home

import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class HomeV2ScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getHomeUrl()}"

    @Test
    fun index() = assertEndpointEquals("/home/screens/index.json", url())
}
