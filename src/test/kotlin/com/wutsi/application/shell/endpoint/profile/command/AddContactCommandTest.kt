package com.wutsi.application.shell.endpoint.profile.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.CreateContactRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AddContactCommandTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var contactApi: WutsiContactApi

    @Test
    fun add() {
        val url = "http://localhost:$port/commands/add-contact?contact-id=555"
        rest.postForEntity(url, null, Any::class.java)

        verify(contactApi).createContact(CreateContactRequest(contactId = 555L))
    }
}
