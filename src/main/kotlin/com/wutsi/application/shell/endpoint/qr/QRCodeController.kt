package com.wutsi.application.shell.endpoint.qr

import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.platform.qrcode.KeyProvider
import com.wutsi.platform.qrcode.QrCode
import com.wutsi.platform.qrcode.QrCodeImageGenerator
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.UUID

@Controller
@RequestMapping("/qr-code")
class QRCodeController(
    private val keyProvider: KeyProvider,
    private val tenantProvider: TenantProvider
) {
    @GetMapping("/{type}/{id}.png")
    fun account(@PathVariable type: String, @PathVariable id: Long): ResponseEntity<Resource> {
        val resource = toResource(id, type)
        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"qrcode-$id-${UUID.randomUUID()}.png\"")
            .body(resource)
    }

    private fun toResource(id: Long, type: String): Resource {
        val data = QrCode(type = type.uppercase(), value = id.toString()).encode(keyProvider)

        val tenant = tenantProvider.get()
        val logoUrl = tenantProvider.logo(tenant)?.let { URL(it) }
        val image = ByteArrayOutputStream()
        QrCodeImageGenerator(logoUrl).generate(data, image)

        return ByteArrayResource(image.toByteArray(), IMAGE_PNG_VALUE)
    }
}
