package com.wutsi.application.shell.endpoint.scan.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.entity.QrEntityType
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
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
import com.wutsi.platform.core.qrcode.ExpiredQrCodeException
import com.wutsi.platform.core.qrcode.KeyProvider
import com.wutsi.platform.core.qrcode.QrCode
import com.wutsi.platform.core.qrcode.QrCodeException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scan/viewer")
class ScanViewerScreen(
    private val keyProvider: KeyProvider
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestBody request: ScanRequest): Widget {
        logger.add("code", request.code)
        logger.add("format", request.format)

        // Parse the qr-code
        var error: String? = null
        var nextUrl: String? = null
        var entity: QrCode? = null
        var imageUrl: String? = null
        try {
            entity = QrCode.decode(request.code, keyProvider)
            nextUrl = nextUrl(entity)
            imageUrl = urlBuilder.build("/qr-code/${entity.type.lowercase()}/${entity.value}.png")

            logger.add("qr_type", entity.type)
            logger.add("qr_value", entity.value)
            logger.add("next_url", nextUrl)
            logger.add("qr_image_url", imageUrl)
        } catch (ex: ExpiredQrCodeException) {
            error = getText("prompt.error.expired-qr-code")
        } catch (ex: QrCodeException) {
            error = getText("prompt.error.unexpected-error")
        }

        // Viewer
        return Screen(
            id = Page.SCAN_VIEWER,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.scan-viewer.app-bar.title")
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
                                )
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

    private fun nextUrl(entity: QrCode?): String? =
        when (entity?.type?.uppercase()) {
            QrEntityType.ACCOUNT.name -> urlBuilder.build("profile?id=${entity.value}")
            QrEntityType.ORDER.name -> urlBuilder.build(storeUrl, "order?id=${entity.value}")
            QrEntityType.PRODUCT.name -> urlBuilder.build(storeUrl, "product?id=${entity.value}")
            QrEntityType.URL.name -> entity.value
            else -> null
        }

    private fun nextButton(nextUrl: String?, entity: QrCode?): WidgetAware =
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
                caption = when (entity?.type?.uppercase()) {
                    QrEntityType.ACCOUNT.name -> getText("page.scan-viewer.button.continue-account")
                    QrEntityType.ORDER.name -> getText("page.scan-viewer.button.continue-order")
                    QrEntityType.PRODUCT.name -> getText("page.scan-viewer.button.continue-product")
                    QrEntityType.URL.name -> getText("page.scan-viewer.button.continue-url")
                    else -> getText("page.scan-viewer.button.continue")
                },
                action = Action(
                    type = if (entity?.type?.lowercase() == "url") ActionType.Navigate else ActionType.Route,
                    url = nextUrl,
                    replacement = true
                )
            )
}
