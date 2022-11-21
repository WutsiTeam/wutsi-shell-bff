package com.wutsi.application.marketplace.settings.catalog.add.page

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.marketplace.settings.catalog.add.dao.PictureRepository
import com.wutsi.application.marketplace.settings.catalog.add.entity.PictureEntity
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.core.storage.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
@RequestMapping("/settings/2/catalog/add/pages/picture")
class AppProduct00PicturePage(
    private val dao: PictureRepository,
    private val storageService: StorageService,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxWidth: Int,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxHeight: Int
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.catalog.add.picture.title")

    override fun getBody(): WidgetAware =
        Container(
            padding = 20.0,
            child = Column(
                children = listOf(
                    Container(
                        borderColor = Theme.COLOR_PRIMARY,
                        border = 1.0,
                        child = Input(
                            name = "file",
                            uploadUrl = urlBuilder.build("/settings/2/catalog/add/pages/picture/upload"),
                            type = InputType.Image,
                            imageSource = ImageSource.Camera,
                            caption = getText("page.settings.catalog.add.picture.camera"),
                            imageMaxWidth = pictureMaxWidth,
                            imageMaxHeight = pictureMaxHeight,
                            action = gotoNextPage()
                        )
                    ),
                    Container(padding = 10.0),
                    Container(
                        borderColor = Theme.COLOR_PRIMARY,
                        border = 1.0,
                        child = Input(
                            name = "file",
                            uploadUrl = urlBuilder.build("/settings/2/catalog/add/pages/picture/upload"),
                            type = InputType.Image,
                            imageSource = ImageSource.Gallery,
                            caption = getText("page.settings.catalog.add.picture.gallery"),
                            imageMaxWidth = pictureMaxWidth,
                            imageMaxHeight = pictureMaxHeight,
                            action = gotoNextPage()
                        )
                    )
                )
            )
        )

    override fun getButton(): Button? = null

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        logger.add("file_name", file.originalFilename)
        logger.add("content_type", contentType)

        // Upload file
        val path = "product/pictures/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)
        logger.add("url", url)

        dao.save(PictureEntity(url.toString()))
    }
}
