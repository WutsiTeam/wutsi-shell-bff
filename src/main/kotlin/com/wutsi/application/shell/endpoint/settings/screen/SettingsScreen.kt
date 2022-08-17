package com.wutsi.application.shell.endpoint.settings.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings")
class SettingsScreen(
    private val tokenProvider: TokenProviderWrapper,
    @Value("\${wutsi.application.login-url}") private val loginUrl: String,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget = Screen(
        id = Page.SETTINGS,
        appBar = AppBar(
            elevation = 0.0,
            backgroundColor = Theme.COLOR_WHITE,
            foregroundColor = Theme.COLOR_BLACK,
            title = getText("page.settings.app-bar.title"),
        ),
        child = Container(
            child = ListView(
                separator = true,
                separatorColor = Theme.COLOR_DIVIDER,
                children = listItems()
            ),
        ),
        bottomNavigationBar = bottomNavigationBar()
    ).toWidget()

    private fun listItems(): List<WidgetAware> {
        val user = securityContext.currentAccount()
        val children = mutableListOf<WidgetAware>(
            Container(
                padding = 5.0,
                child = Column(
                    mainAxisSize = MainAxisSize.min,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    mainAxisAlignment = MainAxisAlignment.spaceAround,
                    children = listOfNotNull(
                        picture(user),
                        Text(
                            caption = user.displayName ?: "",
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                            bold = true
                        ),

                        if (user.business)
                            Container(
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.settings.business-account"),
                                    alignment = TextAlignment.Center,
                                    color = Theme.COLOR_GRAY,
                                )
                            )
                        else
                            null,

                        Text(
                            caption = formattedPhoneNumber(user) ?: "",
                            alignment = TextAlignment.Center,
                        ),
                    ),
                ),
            ),
        )

        // General
        children.add(
            listItem(
                "page.settings.listitem.personal.caption",
                urlBuilder.build("settings/profile"),
                icon = Theme.ICON_PERSON,
            ),
        )

        if (togglesProvider.isAccountEnabled())
            children.add(
                listItem(
                    "page.settings.listitem.account.caption",
                    urlBuilder.build("settings/account"),
                    icon = Theme.ICON_PAYMENT
                ),
            )

        if (togglesProvider.isOrderEnabled())
            children.add(
                listItem(
                    "page.settings.listitem.my-orders.caption",
                    urlBuilder.build(storeUrl, "orders"),
                    icon = Theme.ICON_ORDERS
                ),
            )

        // Applications
        if (user.business) {
            children.add(
                Container(padding = 20.0)
            )
            if (togglesProvider.isStoreEnabled())
                if (user.hasStore)
                    children.add(
                        listItem(
                            "page.settings.listitem.store.caption",
                            urlBuilder.build(storeUrl, "settings/store"),
                            icon = Theme.ICON_STORE
                        ),
                    )
                else
                    children.add(
                        listItemSwitch(
                            "page.settings.listitem.activate-store.caption",
                            urlBuilder.build("/commands/update-profile-attribute?name=has-store"),
                            icon = Theme.ICON_STORE,
                            subCaption = "page.settings.listitem.store.sub-caption"
                        ),
                    )
        }

        // Security/About
        children.addAll(
            listOf(
                Container(padding = 20.0),
                listItem(
                    "page.settings.listitem.security.caption",
                    urlBuilder.build("settings/security"),
                    icon = Theme.ICON_LOCK
                ),
                listItem(
                    "page.settings.listitem.feedback.caption",
                    urlBuilder.build("feedback"),
                    icon = Theme.ICON_FEEDBACK
                ),
                listItem(
                    "page.settings.listitem.about.caption",
                    urlBuilder.build("settings/about"),
                    icon = Theme.ICON_INFO
                ),
            )
        )

        children.add(
            Container(
                padding = 20.0,
                child = Button(
                    caption = getText("page.settings.button.logout"),
                    type = ButtonType.Outlined,
                    action = Action(
                        type = Command,
                        url = urlBuilder.build(loginUrl, "/commands/logout"),
                        parameters = mapOf(
                            "access-token" to (tokenProvider.getToken() ?: "")
                        )
                    )
                )
            )
        )

        return children
    }

    private fun listItem(caption: String, url: String, icon: String? = null) = ListItem(
        padding = 5.0,
        leading = icon?.let { Icon(code = icon, size = 24.0, color = Theme.COLOR_PRIMARY) },
        caption = getText(caption),
        trailing = Icon(
            code = Theme.ICON_CHEVRON_RIGHT,
            size = 24.0,
        ),
        action = Action(
            type = Route,
            url = url
        )
    )

    private fun listItemSwitch(
        caption: String,
        url: String,
        icon: String? = null,
        selected: Boolean = false,
        subCaption: String? = null
    ) =
        ListItemSwitch(
            name = "value",
            icon = icon,
            caption = getText(caption),
            subCaption = subCaption?.let { getText(subCaption) },
            selected = selected,
            action = Action(
                type = Command,
                url = url
            )
        )

    private fun formattedPhoneNumber(user: Account): String? {
        val phone = user.phone ?: return null
        return formattedPhoneNumber(phone.number, phone.country)
    }

    protected fun picture(user: Account): WidgetAware {
        val picture = if (!user.pictureUrl.isNullOrBlank())
            CircleAvatar(
                radius = 32.0,
                child = Image(
                    width = 64.0,
                    height = 64.0,
                    url = user.pictureUrl!!
                )
            )
        else
            CircleAvatar(
                radius = 32.0,
                child = Text(
                    caption = StringUtil.initials(user.displayName),
                    size = 30.0,
                    bold = true
                )
            )

        return Column(
            mainAxisSize = MainAxisSize.min,
            children = listOf(
                picture,
                Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.button.change-picture"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("settings/picture")
                    ),
                    padding = 10.0,
                )
            )
        )
    }
}
