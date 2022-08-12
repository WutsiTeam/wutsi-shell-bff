package com.wutsi.application.shell.endpoint.contact.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.contact.dto.SyncContactRequest
import com.wutsi.platform.contact.WutsiContactApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SyncContactCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var contactApi: WutsiContactApi

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/sync-contacts"
    }

    @Test
    fun sync() {
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        val request = SyncContactRequest(
            phoneNumbers = listOf("a", "b")
        )
        rest.postForEntity(url, request, Any::class.java)

        val req = argumentCaptor<com.wutsi.platform.contact.dto.SyncContactRequest>()
        verify(contactApi).syncContacts(req.capture())

        assertEquals(request.phoneNumbers, req.firstValue.phoneNumbers)
    }

    @Test
    fun contactNotEnabled() {
        doReturn(false).whenever(togglesProvider).isContactEnabled()

        val request = SyncContactRequest(
            phoneNumbers = listOf("a", "b")
        )
        rest.postForEntity(url, request, Any::class.java)

        verify(contactApi, never()).syncContacts(any())
    }
}
