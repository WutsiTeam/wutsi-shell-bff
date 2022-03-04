package com.wutsi.application.shell.endpoint.scan.screen

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.QrService
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
import com.wutsi.application.shell.exception.toErrorResponse
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.DecodeQRCodeRequest
import com.wutsi.platform.qr.dto.Entity
import com.wutsi.platform.qr.error.ErrorURN
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scan/viewer")
class ScanViewerScreen(
    private val qrApi: WutsiQrApi,
    private val tenantProvider: TenantProvider,
    private val qrService: QrService,
    private val mapper: ObjectMapper,
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestBody request: ScanRequest): Widget {
        logger.add("code", request.code)
        logger.add("format", request.format)

        // Parse the qr-code
        val tenant = tenantProvider.get()
        var error: String? = null
        var nextUrl: String? = null
        var entity: Entity? = null
        var imageUrl: String? = null
        try {
            entity = qrApi.decode(
                DecodeQRCodeRequest(
                    token = request.code
                )
            ).entity
            nextUrl = nextUrl(entity)
            imageUrl = qrService.imageUrl(request.code)
        } catch (ex: FeignException) {
            val response = ex.toErrorResponse(mapper)
            error = if (response?.error?.code == ErrorURN.EXPIRED.urn)
                getText("prompt.error.expired-qr-code")
            else if (response?.error?.code == ErrorURN.MALFORMED_TOKEN.urn)
                getText("prompt.error.malformed-qr-code")
            else
                getText("prompt.error.unexpected-error")
        }

        // Viewer
        return Screen(
            id = Page.SCAN_VIEWER,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.scan-viewer.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Center(
                        child = imageUrl?.let {
                            Container(
                                padding = 10.0,
                                alignment = Alignment.Center,
                                borderColor = Theme.COLOR_DIVIDER,
                                border = 1.0,
                                borderRadius = 5.0,
                                child = Image(
                                    url = it,
                                    width = 230.0,
                                    height = 230.0
                                ),
                            )
                        }
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = if (error == null)
                            Icon(Theme.ICON_CHECK_CIRCLE, color = Theme.COLOR_SUCCESS, size = 64.0)
                        else
                            Icon(Theme.ICON_ERROR, color = Theme.COLOR_DANGER, size = 64.0)
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            error?.let { it } ?: getText("page.scan-viewer.valid"),
                            size = Theme.TEXT_SIZE_LARGE
                        )
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = nextButton(nextUrl, entity)
                    )
                )
            )
        ).toWidget()
    }

    private fun includeEmbeddedImage(entity: Entity?): Boolean =
        entity?.type == "payment-request" ||
            entity?.type == "account" ||
            entity?.type == "transaction-approval"

    private fun nextUrl(entity: Entity?): String? =
        if (entity?.type == "payment-request")
            urlBuilder.build(cashUrl, "pay/confirm?payment-request-id=${entity.id}")
        else if (entity?.type == "transaction-approval")
            urlBuilder.build(cashUrl, "send/approval?transaction-id=${entity.id}")
        else if (entity?.type == "account")
            urlBuilder.build("profile?id=${entity.id}")
        else if (entity?.type == "url")
            entity.id
        else
            null

    private fun nextButton(nextUrl: String?, entity: Entity?): WidgetAware =
        if (nextUrl == null)
            Button(
                caption = getText("page.scan-viewer.button.close"),
                type = ButtonType.Text,
                action = Action(
                    type = ActionType.Route,
                    url = "route:/~"
                )
            )
        else
            Button(
                caption = when (entity?.type?.lowercase()) {
                    "account" -> getText("page.scan-viewer.button.continue-account")
                    "payment-request" -> getText("page.scan-viewer.button.continue-payment")
                    "transaction-approval" -> getText("page.scan-viewer.button.continue-transaction-approval")
                    "url" -> getText("page.scan-viewer.button.continue-url")
                    else -> getText("page.scan-viewer.button.continue")
                },
                action = Action(
                    type = if (entity?.type?.lowercase() == "url") ActionType.Navigate else ActionType.Route,
                    url = nextUrl,
                    replacement = true
                )
            )
}
