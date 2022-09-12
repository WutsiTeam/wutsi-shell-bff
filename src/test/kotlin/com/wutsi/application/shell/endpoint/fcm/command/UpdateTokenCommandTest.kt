package com.wutsi.application.shell.endpoint.fcm.command

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.fcm.dto.UpdateTokenRequest
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateTokenCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/update-fcm-token"
    }

    @Test
    fun sync() {
        val request = UpdateTokenRequest(
            token = "123"
        )
        rest.postForEntity(url, request, Any::class.java)

        val req = argumentCaptor<UpdateAccountAttributeRequest>()
        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "fcm-token", UpdateAccountAttributeRequest(request.token))
    }
}
