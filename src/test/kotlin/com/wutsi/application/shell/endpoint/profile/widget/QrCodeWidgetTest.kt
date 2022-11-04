package com.wutsi.application.shell.endpoint.profile.widget

import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class QrCodeWidgetTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/profile/qr-code-widget?id=$USER_ID"
    }

    @Test
    fun profile() {
        // THEN
        assertEndpointEquals("/shell/profile/qr-code.json", url)
    }
}
