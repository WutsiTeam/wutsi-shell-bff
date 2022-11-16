package com.wutsi.application.membership.onboard.screen

import com.wutsi.application.AbstractEndpointTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class OnboardV2ScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/onboard/2"

    @Test
    fun index() = assertEndpointEquals("/membership/onboard/screens/index.json", url())
}
