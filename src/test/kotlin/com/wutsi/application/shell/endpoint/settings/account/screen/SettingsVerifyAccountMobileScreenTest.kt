package com.wutsi.application.shell.endpoint.settings.account.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.entity.SmsCodeEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsVerifyAccountMobileScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/accounts/verify/mobile"

        val state = SmsCodeEntity(phoneNumber = "+237995099990", token = "777")
        doReturn(state).whenever(cache).get(any(), eq(SmsCodeEntity::class.java))
    }

    @Test
    fun index() = assertEndpointEquals("/shell/screens/settings/accounts/verify/mobile.json", url)
}
