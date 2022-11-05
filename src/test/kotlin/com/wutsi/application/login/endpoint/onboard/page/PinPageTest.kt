package com.wutsi.application.login.endpoint.onboard.page

import com.wutsi.application.login.endpoint.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PinPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        url = "http://localhost:$port/pages/pin"
    }

    @Test
    fun index() = assertEndpointEquals("/login/pages/pin.json", url)
}
