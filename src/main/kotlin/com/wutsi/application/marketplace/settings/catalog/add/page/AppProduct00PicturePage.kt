package com.wutsi.application.marketplace.settings.catalog.add.page

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.marketplace.settings.catalog.add.dao.PictureRepository
import com.wutsi.application.marketplace.settings.catalog.add.entity.PictureEntity
import com.wutsi.application.widget.UploadWidget
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
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
            child = UploadWidget(
                name = "file",
                uploadUrl = urlBuilder.build("/settings/2/catalog/add/pages/picture/upload"),
                imageMaxWidth = pictureMaxWidth,
                imageMaxHeight = pictureMaxHeight,
                action = gotoNextPage(),
                messages = messages
            )
        )

    override fun getButton(): Button? = null

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        val path = "product/pictures/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)

        dao.save(PictureEntity(url.toString()))
    }
}
