package com.wutsi.application.membership.settings.picture.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.storage.StorageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
@RequestMapping("/settings/2/picture")
class SettingsV2PictureScreen(
    private val membershipManagerApi: MembershipManagerApi,
    private val storageService: StorageService
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        val me = membershipManagerApi.getMember().member
        return Screen(
            id = Page.SETTINGS_PICTURE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.picture.app-bar.title")
            ),
            child = Column(
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Image(
                            url = me.pictureUrl ?: "",
                            width = 256.0,
                            height = 256.0
                        )
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Input(
                        name = "file",
                        uploadUrl = urlBuilder.build("${Page.getSettingsUrl()}/picture/upload"),
                        type = InputType.Image,
                        imageSource = ImageSource.Camera,
                        caption = getText("page.settings.picture.camera"),
                        imageMaxWidth = 512,
                        imageMaxHeight = 512,
                        action = gotoPreviousScreen()
                    ),
                    Input(
                        name = "file",
                        uploadUrl = urlBuilder.build("${Page.getSettingsUrl()}/picture/upload"),
                        type = InputType.Image,
                        imageSource = ImageSource.Gallery,
                        caption = getText("page.settings.picture.gallery"),
                        imageMaxWidth = 512,
                        imageMaxHeight = 512,
                        action = gotoPreviousScreen()
                    ),
                    Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.picture.cancel"),
                        action = gotoPreviousScreen()
                    ),
                    Divider(color = Theme.COLOR_DIVIDER)
                )
            )
        ).toWidget()
    }

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        logger.add("file_name", file.originalFilename)
        logger.add("file_content_type", contentType)

        // Upload file
        val memberId = SecurityUtil.getMemberId()
        val path = "user/$memberId/picture/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)
        logger.add("picture_url", url)

        // Update user profile
        membershipManagerApi.updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "picture-url",
                value = url.toString()
            )
        )
    }
}
