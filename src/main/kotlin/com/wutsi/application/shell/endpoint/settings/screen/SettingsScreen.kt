package com.wutsi.application.shell.endpoint.settings.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.service.TogglesProvider
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
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
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
    private val togglesProvider: TogglesProvider,

    @Value("\${wutsi.application.login-url}") private val loginUrl: String,
    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
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
                    children = listOf(
                        picture(user),
                        Text(
                            caption = user.displayName ?: "",
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                            bold = true
                        ),
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
                icon = Theme.ICON_PERSON
            ),
        )
        if (togglesProvider.isAccountEnabled())
            children.add(
                listItem(
                    "page.settings.listitem.account.caption",
                    urlBuilder.build("settings/account"),
                    icon = Theme.ICON_ACCOUNT
                ),
            )
        children.add(
            listItem(
                "page.settings.listitem.my-transactions.caption",
                urlBuilder.build(cashUrl, "history"),
                icon = Theme.ICON_HISTORY
            ),
        )
        if (togglesProvider.isStoreEnabled())
            children.add(
                listItem(
                    "page.settings.listitem.my-purchases.caption",
                    urlBuilder.build(storeUrl, "purchases"),
                    icon = Theme.ICON_SHOPPING_BAG
                ),
            )

        // Applications
        if (user.business) {
            children.add(
                Container(padding = 20.0)
            )
            if (togglesProvider.isStoreEnabled())
                children.add(
                    listItem(
                        "page.settings.listitem.store.caption",
                        urlBuilder.build(storeUrl, "settings/store"),
                        icon = Theme.ICON_STORE
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
                    "page.settings.listitem.about.caption",
                    urlBuilder.build("settings/about"),
                    icon = Theme.ICON_INFO
                ),
            )
        )
        if (togglesProvider.isLogoutEnabled())
            children.add(
                Container(
                    padding = 20.0,
                    child = Button(
                        caption = getText("page.settings.button.logout"),
                        type = ButtonType.Outlined,
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build(loginUrl, "/commands/logout")
                        )
                    )
                )
            )

        return children
    }

    private fun listItem(caption: String, url: String, icon: String? = null) = ListItem(
        padding = 5.0,
        leading = icon?.let { Icon(code = icon, size = 24.0, color = Theme.COLOR_BLACK) },
        caption = getText(caption),
        trailing = Icon(
            code = Theme.ICON_CHEVRON_RIGHT,
            size = 24.0,
            color = Theme.COLOR_BLACK
        ),
        action = Action(
            type = Route,
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
                        type = ActionType.Route,
                        url = urlBuilder.build("settings/picture")
                    ),
                    padding = 10.0,
                )
            )
        )
    }
}
