package com.wutsi.application.shell.endpoint.scan.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
import com.wutsi.flutter.sdui.Widget
import com.wutsi.platform.core.qrcode.KeyProvider
import com.wutsi.platform.core.qrcode.QrCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ScanViewerScreenTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var keyProvider: KeyProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/scan/viewer"

        doReturn("1").whenever(keyProvider).getKeyId()
        doReturn("123456").whenever(keyProvider).getKey(any())
    }

    @Test
    fun account() {
        // WHEN
        val request = ScanRequest(
            code = QrCode("ACCOUNT", "1").encode(keyProvider)
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/shell/screens/scan/viewer-account.json", response.body)
    }

    @Test
    fun order() {
        // WHEN
        val request = ScanRequest(
            code = QrCode("ORDER", "1").encode(keyProvider)
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/shell/screens/scan/viewer-order.json", response.body)
    }

    @Test
    fun product() {
        // WHEN
        val request = ScanRequest(
            code = QrCode("PRODUCT", "1").encode(keyProvider)
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/shell/screens/scan/viewer-product.json", response.body)
    }

    @Test
    fun url() {
        // WHEN
        val request = ScanRequest(
            code = "https://www.google.ca"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/shell/screens/scan/viewer-url.json", response.body)
    }

    @Test
    fun invalid() {
        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/shell/screens/scan/viewer-invalid.json", response.body)
    }
}
