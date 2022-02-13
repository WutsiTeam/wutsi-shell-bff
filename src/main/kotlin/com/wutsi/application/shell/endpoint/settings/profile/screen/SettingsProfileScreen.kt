package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.CityService
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/profile")
class SettingsProfileScreen(
    private val urlBuilder: URLBuilder,
    private val securityContext: SecurityContext,
    private val sharedUIMapper: SharedUIMapper,
    private val cityService: CityService,
    private val togglesProvider: TogglesProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val children = mutableListOf<WidgetAware>()
        children.add(
            Container(padding = 10.0)
        )

        val account = securityContext.currentAccount()
        val city = cityService.get(account.cityId)
        val locale = LocaleContextHolder.getLocale()
        children.addAll(
            listOf(
                listItem(
                    "page.settings.profile.attribute.display-name",
                    account.displayName,
                    "settings/profile/display-name"
                ),
                listItem(
                    "page.settings.profile.attribute.language",
                    StringUtil.capitalizeFirstLetter(
                        Locale(account.language).getDisplayLanguage(locale)
                    ),
                    "settings/profile/language"
                ),
                listItem("page.settings.profile.attribute.street", account.street, "settings/profile/street"),
                listItem(
                    "page.settings.profile.attribute.city-id",
                    sharedUIMapper.toLocationText(city, account.country),
                    "settings/profile/city"
                ),
                listItem(
                    "page.settings.profile.attribute.timezone-id",
                    account.timezoneId,
                    "settings/profile/timezone"
                ),
            )
        )

        if (togglesProvider.isBusinessAccountEnabled()) {
            if (account.business)
                children.addAll(
                    listOf(
                        listItem(
                            "page.settings.profile.attribute.category-id",
                            account.category?.let { sharedUIMapper.toTitle(it) },
                            "settings/profile/category"
                        ),
                        listItem(
                            "page.settings.profile.attribute.biography",
                            account.biography,
                            "settings/profile/biography"
                        ),
                        listItem(
                            "page.settings.profile.attribute.website",
                            account.website,
                            "settings/profile/website"
                        ),
                        listItem(
                            "page.settings.profile.attribute.whatsapp",
                            account.whatsapp,
                            "settings/profile/whatsapp"
                        ),
                    )
                )

            children.add(
                ListItemSwitch(
                    caption = getText("page.settings.profile.attribute.business"),
                    name = "value",
                    selected = account.business,
                    action = Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("commands/update-profile-attribute?name=business")
                    )
                )
            )
        }

        return Screen(
            id = Page.SETTINGS_PROFILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.app-bar.title"),
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = children
                )
            )
        ).toWidget()
    }

    private fun listItem(caption: String, value: Any?, commandUrl: String): ListItem =
        ListItem(
            caption = getText(caption),
            subCaption = value?.toString(),
            trailing = Icon(
                code = Theme.ICON_EDIT,
                size = 24.0,
                color = Theme.COLOR_BLACK
            ),
            action = Action(
                type = ActionType.Route,
                url = urlBuilder.build(commandUrl)
            )
        )
}
