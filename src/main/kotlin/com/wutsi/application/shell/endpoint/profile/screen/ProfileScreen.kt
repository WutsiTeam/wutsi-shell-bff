package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.CategoryService
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileScreen(
    private val urlBuilder: URLBuilder,
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi,
    private val categoryService: CategoryService,
    private val userProvider: UserProvider,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val user = accountApi.getAccount(id).account
        return Screen(
            id = Page.PROFILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                title = getText("page.profile.app-bar.title"),
            ),
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    profile(user),
                    details(user)
                )
            )
        ).toWidget()
    }

    private fun profile(user: Account): WidgetAware {
        val buttons = mutableListOf<WidgetAware>()
        if (!isContact(user))
            buttons.add(
                Button(
                    caption = getText("page.profile.button.add-contact"),
                    padding = 5.0,
                    stretched = false,
                    action = Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("commands/add-contact?contact-id=${user.id}")
                    ),
                )
            )

        val children = mutableListOf<WidgetAware>()
        children.addAll(
            listOf(
                Container(
                    padding = 5.0,
                    child = picture(user, 48.0)
                ),
                Container(
                    padding = 5.0,
                    child = Column(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = buttons,
                    )
                )
            )
        )

        return Container(
            padding = 10.0,
            background = Theme.COLOR_PRIMARY,
            child = Row(
                mainAxisAlignment = MainAxisAlignment.start,
                children = children,
            )
        )
    }

    private fun details(user: Account): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            Container(
                padding = 10.0,
                alignment = Alignment.CenterLeft,
                child = Text(
                    user.displayName ?: "",
                    bold = true,
                    size = Theme.TEXT_SIZE_LARGE
                ),
            )
        )

        if (user.business) {
            val business = mutableListOf<WidgetAware>()
            val category = user.categoryId?.let { categoryService.get(it) }
            if (category != null)
                business.add(
                    Container(
                        child = Text(
                            if (user.language == "fr") category.titleFrench else category.title,
                            alignment = TextAlignment.Left,
                            color = Theme.COLOR_GRAY,
                        )
                    )
                )

            if (!user.biography.isNullOrEmpty())
                business.add(
                    Container(
                        alignment = Alignment.TopLeft,
                        child = Text(
                            user.biography ?: "",
                            alignment = TextAlignment.Left,
                        )
                    )
                )

            if (!user.website.isNullOrEmpty())
                business.add(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.spaceBetween,
                        mainAxisSize = MainAxisSize.min,
                        children = listOf(
                            Icon(code = Theme.ICON_LINK),
                            Button(
                                type = ButtonType.Text,
                                caption = sanitizeWebsite(user.website!!),
                                stretched = false,
                                action = Action(
                                    type = ActionType.Navigate,
                                    url = user.website!!
                                )
                            )
                        ),
                    )
                )

            if (business.isNotEmpty())
                children.add(
                    Container(
                        padding = 10.0,
                        child = Column(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.start,
                            children = business
                        )
                    )
                )
        } else {
            children.add(
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.profile.button.send"),
                        type = ButtonType.Outlined,
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build(cashUrl, "send?recipient-id=${user.id}")
                        ),
                    )
                )
            )
        }

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = children,
        )
    }

    private fun sanitizeWebsite(website: String): String {
        val i = website.indexOf("//")
        return if (i > 0)
            website.substring(i + 2)
        else
            website
    }

    private fun isContact(user: Account): Boolean =
        if (user.id == userProvider.id())
            false
        else
            contactApi.searchContact(
                request = SearchContactRequest(
                    contactIds = listOf(user.id)
                )
            ).contacts.isNotEmpty()

    private fun picture(user: Account, size: Double): WidgetAware =
        if (!user.pictureUrl.isNullOrBlank())
            CircleAvatar(
                radius = size / 2,
                child = Image(
                    width = size,
                    height = size,
                    url = user.pictureUrl!!
                )
            )
        else
            CircleAvatar(
                radius = size / 2,
                child = Text(
                    caption = StringUtil.initials(user.displayName),
                    size = (size / 2 - 2),
                    bold = true
                )
            )
}