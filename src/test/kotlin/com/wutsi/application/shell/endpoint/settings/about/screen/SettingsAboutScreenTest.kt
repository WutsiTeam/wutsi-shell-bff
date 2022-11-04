package com.wutsi.application.shell.endpoint.settings.about.screen

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.endpoint.settings.about.AppInfoRestInterceptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsAboutScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/about"

        rest.interceptors.add(AppInfoRestInterceptor())
    }

    @Test
    fun index() = assertEndpointEquals("/shell/screens/settings/about/about.json", url)

    @Test
    fun switchToProd() {
        doReturn(true).whenever(togglesProvider).isSwitchEnvironmentEnabled()
        assertEndpointEquals("/shell/screens/settings/about/about-env-enabled.json", url)
    }
}
