package com.wutsi.application.marketplace.settings.catalog.product.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.shared.Theme
import com.wutsi.application.widget.PictureListViewWidget
import com.wutsi.application.widget.PictureWidget
import com.wutsi.application.widget.UploadWidget
import com.wutsi.ecommerce.catalog.entity.ProductStatus
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ExpandablePanel
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.AddPictureRequest
import com.wutsi.marketplace.manager.dto.PictureSummary
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.messaging.UrlShortener
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.text.DecimalFormat
import java.util.UUID

@RestController
@RequestMapping("/settings/2/catalog/product")
class SettingsV2ProductScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
    private val storageService: StorageService,
    private val urlShortener: UrlShortener,

    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxWidth: Int,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxHeight: Int,
    @Value("\${wutsi.application.webapp-url}") private val webAppUrl: String
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(
        @RequestParam id: Long,
        @RequestParam(required = false) errors: Array<String>? = null
    ): Widget {
        val member = getCurrentMember()
        val product = marketplaceManagerApi.getProduct(id).product

        return Screen(
            id = Page.SETTINGS_CATALOG_PRODUCT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                title = getText("page.settings.catalog.product.app-bar.title")
            ),
            child = toProductListWidget(product, member)
        ).toWidget()
    }

    private fun toProductListWidget(product: Product, member: Member): WidgetAware {
        val country = regulationEngine.country(member.country)
        val price = product.price?.let { DecimalFormat(country.monetaryFormat).format(it) }

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOfNotNull(
                toPictureListViewWidget(product),
                toCTAWidget(product),
                Divider(color = Theme.COLOR_DIVIDER),
                Flexible(
                    flex = 10,
                    child = ListView(
                        separatorColor = Theme.COLOR_DIVIDER,
                        separator = true,
                        children = listOfNotNull(
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.title",
                                product.title,
                                urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor?name=title&id=${product.id}")
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.price",
                                price,
                                urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor?name=price&id=${product.id}")
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.quantity",
                                product.quantity?.toString(),
                                urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor?name=quantity&id=${product.id}")
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.summary",
                                product.summary,
                                urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor?name=summary&id=${product.id}")
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.description",
                                description(product.description),
                                urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/editor?name=description&id=${product.id}")
                            ),
                            toDangerWidget(product)
                        )
                    )
                )
            )
        )
    }

    private fun toCTAWidget(product: Product): WidgetAware? =
        if (product.status == ProductStatus.DRAFT.name) {
            Container(
                padding = 10.0,
                child = Button(
                    caption = getText("page.settings.catalog.product.button.publish"),
                    action = executeCommand(
                        url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/publish?id=${product.id}")
                    )
                )
            )
        } else if (product.status == ProductStatus.PUBLISHED.name) {
            Container(
                padding = 10.0,
                child = Button(
                    caption = getText("page.settings.catalog.product.button.share"),
                    action = Action(
                        type = ActionType.Share,
                        message = urlShortener.shorten("$webAppUrl/product?id=${product.id}")
                    )
                )
            )
        } else {
            null
        }

    private fun toDangerWidget(product: Product): WidgetAware =
        Container(
            padding = 10.0,
            child = ExpandablePanel(
                header = getText("page.settings.catalog.product.button.more"),
                expanded = Container(
                    padding = 20.0,
                    borderColor = Theme.COLOR_DANGER,
                    border = 1.0,
                    background = Theme.COLOR_DANGER_LIGHT,
                    child = Column(
                        children = listOfNotNull(
                            if (product.status == "PUBLISHED") {
                                Button(
                                    caption = getText("page.settings.catalog.product.button.unpublish"),
                                    action = executeCommand(
                                        url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/unpublish?id=${product.id}"),
                                        confirm = getText("page.settings.catalog.product.confirm-unpublish")
                                    )
                                )
                            } else {
                                null
                            },

                            if (product.status == "PUBLISHED") {
                                Container(padding = 10.0)
                            } else {
                                null
                            },

                            Button(
                                caption = getText("page.settings.catalog.product.button.delete"),
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/delete?id=${product.id}"),
                                    confirm = getText("page.settings.catalog.product.confirm-delete")
                                )
                            )
                        )
                    )
                )
            )
        )

    private fun toListItemWidget(caption: String, value: String?, url: String) = ListItem(
        caption = getText(caption),
        subCaption = value,
        trailing = Icon(
            code = Theme.ICON_EDIT,
            size = 24.0,
            color = Theme.COLOR_BLACK
        ),
        action = Action(
            type = ActionType.Route,
            url = url
        )
    )

    private fun toPictureListViewWidget(product: Product): WidgetAware {
        val images = mutableListOf<PictureWidget>()

        // Thumbnail as 1st image
        if (product.thumbnail != null) {
            images.add(toPictureWidget(product.thumbnail!!))
        }

        // Other pictures
        images.addAll(
            product.pictures
                .filter { it.id != product.thumbnail?.id }
                .map {
                    toPictureWidget(it)
                }
        )

        return Container(
            padding = 10.0,
            height = PictureListViewWidget.IMAGE_HEIGHT + 2 * (10.0 + PictureListViewWidget.IMAGE_PADDING),
            child = PictureListViewWidget(
                children = images,
                action = if (product.pictures.size < regulationEngine.maxPictures()) {
                    Action(
                        type = ActionType.Prompt,
                        prompt = toUploadDialogWidget(product).toWidget()
                    )
                } else {
                    null
                }
            )
        )
    }

    private fun toPictureWidget(picture: PictureSummary) = PictureWidget(
        padding = PictureListViewWidget.IMAGE_PADDING,
        width = PictureListViewWidget.IMAGE_WIDTH,
        height = PictureListViewWidget.IMAGE_HEIGHT,
        border = 1.0,
        url = picture.url,
        action = gotoUrl(
            urlBuilder.build(
                "${Page.getSettingsCatalogUrl()}/picture?id=${picture.id}&url=" + encodeURLParam(
                    picture.url
                )
            )
        )
    )

    private fun toUploadDialogWidget(product: Product) = Dialog(
        title = getText("page.settings.catalog.product.add-picture"),
        actions = listOf(
            UploadWidget(
                name = "file",
                uploadUrl = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product/upload?id=${product.id}"),
                imageMaxWidth = pictureMaxWidth,
                imageMaxHeight = pictureMaxHeight,
                action = gotoUrl(
                    url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product?id=${product.id}"),
                    replacement = true
                )
            ),
            Button(
                type = ButtonType.Text,
                caption = getText("page.settings.store.product.button.cancel")
            )
        )
    )

    private fun description(value: String?): String? =
        if (value == null)
            null
        else if (value.length < 160)
            value
        else
            value.substring(0, 160) + "..."

    @PostMapping("/upload")
    fun upload(@RequestParam id: Long, @RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        val path = "product/$id/pictures/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)

        marketplaceManagerApi.addPicture(
            request = AddPictureRequest(
                productId = id,
                url = url.toString()
            )
        )
    }

    @PostMapping("/publish")
    fun publish(@RequestParam id: Long): Action {
        marketplaceManagerApi.publishProduct(id)
        return gotoUrl(
            url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product?id=$id"),
            replacement = true
        )
    }

    @PostMapping("/unpublish")
    fun unpublish(@RequestParam id: Long): Action {
        marketplaceManagerApi.unpublishProduct(id)
        return gotoUrl(
            url = urlBuilder.build("${Page.getSettingsCatalogUrl()}/product?id=$id"),
            replacement = true
        )
    }

    @PostMapping("/delete")
    fun delete(@RequestParam id: Long): Action {
        marketplaceManagerApi.deleteProduct(id)
        return gotoPreviousScreen()
    }
}
