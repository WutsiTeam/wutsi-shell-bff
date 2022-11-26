package com.wutsi.application.membership.settings.profile.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.membership.settings.business.dto.SubmitBusinessAttributeRequest
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
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.regulation.CountryNotSupportedException
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/2/profile")
class SettingsV2ProfileScreen(
    private val regulationEngine: RegulationEngine
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val children = mutableListOf<WidgetAware>()
        children.add(
            Container(padding = 10.0)
        )

        val member = getCurrentMember()
        val locale = LocaleContextHolder.getLocale()
        children.addAll(
            listOf(
                listItem(
                    "page.settings.profile.attribute.display-name",
                    member.displayName,
                    "${Page.getSettingsProfileEditorUrl()}?name=display-name"
                ),
                listItem(
                    "page.settings.profile.attribute.email",
                    member.email,
                    "${Page.getSettingsProfileEditorUrl()}?name=email"
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
                        "${Page.getSettingsProfileEditorUrl()}?name=category"
                    ),
                    listItem(
                        "page.settings.profile.attribute.biography",
                        member.biography,
                        "${Page.getSettingsProfileEditorUrl()}?name=biography"
                    ),
                    listItem(
                        "page.settings.profile.attribute.website",
                        member.website,
                        "${Page.getSettingsProfileEditorUrl()}?name=website"
                    ),
                    listItem(
                        "page.settings.profile.attribute.facebook-id",
                        member.facebookId?.let { "https://www.facebook.com/${member.facebookId}" },
                        "${Page.getSettingsProfileEditorUrl()}?name=facebook-id"
                    ),
                    listItem(
                        "page.settings.profile.attribute.instagram-id",
                        member.instagramId?.let { "https://www.instagram.com/${member.instagramId}" },
                        "${Page.getSettingsProfileEditorUrl()}?name=instagram-id"
                    ),
                    listItem(
                        "page.settings.profile.attribute.twitter-id",
                        member.twitterId?.let { "https://www.twitter.com/${member.twitterId}" },
                        "${Page.getSettingsProfileEditorUrl()}?name=twitter-id"
                    ),
                    listItem(
                        "page.settings.profile.attribute.youtube-id",
                        member.twitterId?.let { "https://www.youtube.com/@${member.youtubeId}" },
                        "${Page.getSettingsProfileEditorUrl()}?name=youtube-id"
                    ),
                    ListItemSwitch(
                        caption = getText("page.settings.profile.attribute.whatsapp"),
                        subCaption = getText("page.settings.profile.attribute.whatsapp.description"),
                        name = "value",
                        selected = member.whatsapp,
                        action = executeCommand(
                            urlBuilder.build("${Page.getSettingsProfileUrl()}/submit?name=whatsapp")
                        )
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
                    "${Page.getSettingsProfileEditorUrl()}?name=language"
                ),
                listItem(
                    "page.settings.profile.attribute.timezone-id",
                    member.timezoneId,
                    "${Page.getSettingsProfileEditorUrl()}?name=timezone-id"
                ),
                listItem(
                    "page.settings.profile.attribute.city-id",
                    member.city?.name,
                    "${Page.getSettingsProfileEditorUrl()}?name=city-id"
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
                    subCaption = getText("page.settings.profile.attribute.business.description"),
                    name = "value",
                    selected = member.business,
                    action = gotoUrl(
                        urlBuilder.build(Page.getSettingsBusinessUrl())
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

    @PostMapping("/submit")
    fun submit(
        @RequestParam name: String,
        @RequestBody request: SubmitBusinessAttributeRequest
    ): Action {
        membershipManagerApi.updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = name,
                value = request.value
            )
        )
        return gotoUrl(
            url = urlBuilder.build(Page.getSettingsProfileUrl()),
            replacement = true
        )
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
            subCaption = if (value?.toString().isNullOrEmpty()) null else value?.toString(),
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
