package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BusinessStreetPageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/business/pages/street"
    }

    @Test
    fun invoke() {
        assertEndpointEquals("/pages/settings/profile/street.json", url)
    }
}
