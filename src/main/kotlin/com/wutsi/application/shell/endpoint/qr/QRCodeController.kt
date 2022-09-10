package com.wutsi.application.shell.endpoint.qr

import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.platform.core.qrcode.KeyProvider
import com.wutsi.platform.core.qrcode.QrCode
import com.wutsi.platform.core.qrcode.QrCodeImageGenerator
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.UUID

@Controller
@RequestMapping("/qr-code")
class QRCodeController(
    private val keyProvider: KeyProvider,
    private val tenantProvider: TenantProvider
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(QRCodeController::class.java)
    }

    @GetMapping("/{type}/{id}.png")
    fun account(
        @PathVariable type: String,
        @PathVariable id: Long,
        @RequestParam(name = "tenant-id", required = false) tenantId: Long? = null
    ): ResponseEntity<Resource> {
        val resource = toResource(id, type, tenantId)
        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"qrcode-$id-${UUID.randomUUID()}.png\"")
            .body(resource)
    }

    private fun toResource(id: Long, type: String, tenantId: Long?): Resource {
        val data = QrCode(type = type.uppercase(), value = id.toString()).encode(keyProvider)

        val logoUrl = getTenantLogoUrl(tenantId)
        val image = ByteArrayOutputStream()
        QrCodeImageGenerator(logoUrl).generate(data, image)

        return ByteArrayResource(image.toByteArray(), IMAGE_PNG_VALUE)
    }

    private fun getTenantLogoUrl(tenantId: Long?): URL? {
        if (tenantId != null) {
            try {
                val tenant = tenantProvider.get(tenantId)
                return tenantProvider.logo(tenant)?.let { URL(it) }
            } catch (ex: Exception) {
                LOGGER.warn("Unable to resolve Tenant#$tenantId", ex)
            }
        }
        return null
    }
}
