package com.wutsi.application.membership.settings.profile.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.StringUtil
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
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.CountryNotSupportedException
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/2/profile")
class SettingsV2ProfileScreen(
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        val children = mutableListOf<WidgetAware>()
        children.add(
            Container(padding = 10.0)
        )

        val member = membershipManagerApi.getMember().member
        val locale = LocaleContextHolder.getLocale()
        children.addAll(
            listOf(
                listItem(
                    "page.settings.profile.attribute.display-name",
                    member.displayName,
                    "${Page.getSettingsUrl()}/profile/editor?name=display-name"
                ),
                listItem(
                    "page.settings.profile.attribute.email",
                    member.email,
                    "${Page.getSettingsUrl()}/profile/editor?name=email"
                )
            )
        )

        if (member.business) {
            children.addAll(
                listOf(
                    Container(
                        padding = 20.0
                    ),
                    listItem(
                        "page.settings.profile.attribute.category-id",
                        member.category?.let { it.title },
                        "${Page.getSettingsUrl()}/profile/editor?name=category"
                    ),
                    listItem(
                        "page.settings.profile.attribute.biography",
                        member.biography,
                        "${Page.getSettingsUrl()}/profile/editor?name=biography"
                    ),
                    listItem(
                        "page.settings.profile.attribute.whatsapp",
                        member.whatsapp,
                        "${Page.getSettingsUrl()}/profile/editor?name=whatsapp"
                    ),
                    listItem(
                        "page.settings.profile.attribute.website",
                        member.website,
                        "${Page.getSettingsUrl()}/profile/editor?name=website"
                    ),
                    listItem(
                        "page.settings.profile.attribute.facebook-id",
                        member.facebookId?.let { "https://www.facebook.com/${member.facebookId}" },
                        "${Page.getSettingsUrl()}/profile/editor?name=facebook"
                    ),
                    listItem(
                        "page.settings.profile.attribute.instagram-id",
                        member.instagramId?.let { "https://www.instagram.com/${member.instagramId}" },
                        "${Page.getSettingsUrl()}/profile/editor?name=instagram"
                    ),
                    listItem(
                        "page.settings.profile.attribute.twitter-id",
                        member.twitterId?.let { "https://www.twitter.com/${member.twitterId}" },
                        "${Page.getSettingsUrl()}/profile/editor?name=twitter"
                    )
                )
            )
        }

        children.addAll(
            listOf(
                Container(
                    padding = 20.0
                ),
                listItem(
                    "page.settings.profile.attribute.language",
                    StringUtil.capitalizeFirstLetter(
                        Locale(member.language).getDisplayLanguage(locale)
                    ),
                    "${Page.getSettingsUrl()}/profile/editor?name=language"
                ),
                listItem(
                    "page.settings.profile.attribute.timezone-id",
                    member.timezoneId,
                    "${Page.getSettingsUrl()}/profile/editor?name=timezone-id"
                ),
                listItem(
                    "page.settings.profile.attribute.city-id",
                    member.city?.name,
                    "${Page.getSettingsUrl()}/profile/editor?name=city-id"
                ),
                listItem(
                    "page.settings.profile.attribute.country",
                    Locale(member.language, member.country).getDisplayCountry(locale),
                    null
                )
            )
        )

        if (canEnableBusiness(member)) {
            children.add(
                Container(
                    padding = 20.0
                )
            )
            children.add(
                ListItemSwitch(
                    caption = getText("page.settings.profile.attribute.business"),
                    name = "value",
                    selected = member.business,
                    action = gotoUrl("${Page.getSettingsUrl()}/profile/business")
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
                title = getText("page.settings.profile.app-bar.title")
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

    private fun canEnableBusiness(member: Member): Boolean =
        try {
            !member.business && regulationEngine.country(member.country).supportsBusinessAccount
        } catch (ex: CountryNotSupportedException) {
            false
        }

    private fun listItem(caption: String, value: Any?, commandUrl: String?): ListItem =
        ListItem(
            caption = getText(caption),
            subCaption = value?.toString(),
            trailing = commandUrl?.let {
                Icon(
                    code = Theme.ICON_EDIT,
                    size = 24.0,
                    color = Theme.COLOR_BLACK
                )
            },
            action = commandUrl?.let {
                Action(
                    type = ActionType.Route,
                    url = urlBuilder.build(commandUrl)
                )
            }
        )
}
