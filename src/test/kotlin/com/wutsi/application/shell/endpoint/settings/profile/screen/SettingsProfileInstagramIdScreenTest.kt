package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class SettingsProfileInstagramIdScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/profile/instagram"
    }

    @Test
    fun index() {
        assertEndpointEquals("/shell/screens/settings/profile/instagram-id.json", url)
    }
}
