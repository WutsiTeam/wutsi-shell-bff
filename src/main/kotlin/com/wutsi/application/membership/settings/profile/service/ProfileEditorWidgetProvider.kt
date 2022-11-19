package com.wutsi.application.membership.settings.profile.service

import com.wutsi.application.shared.service.StringUtil
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.SearchCategoryRequest
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.TimeZone

@Service
class ProfileEditorWidgetProvider(
    private val regulationEngine: RegulationEngine,
    private val membershipManagerApi: MembershipManagerApi,
    private val messages: MessageSource
) {
    fun get(name: String, member: Member): WidgetAware =
        when (name) {
            "biography" -> getInputWidget(member.biography, 160, maxLines = 2)
            "category-id" -> getCategoryWidget(member)
            "city-id" -> getCityWidget(member)
            "display-name" -> getInputWidget(member.displayName, 50, required = true)
            "email" -> getInputWidget(member.email, 160, InputType.Email)
            "facebook-id" -> getInputWidget(member.facebookId, 30)
            "instagram-id" -> getInputWidget(member.instagramId, 30)
            "language" -> getLanguageWidget(member)
            "timezone-id" -> getTimezoneWidget(member)
            "twitter-id" -> getInputWidget(member.twitterId, 30)
            "whatsapp" -> getWhatsappWidget(member)
            "website" -> getInputWidget(member.website, 160, InputType.Url)
            "youtube-id" -> getInputWidget(member.youtubeId, 30)
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun getLanguageWidget(member: Member) = DropdownButton(
        name = "value",
        value = member.language,
        required = true,
        children = regulationEngine.supportedLanguages().map {
            DropdownMenuItem(
                caption = StringUtil.capitalizeFirstLetter(Locale(it).getDisplayLanguage(getLocale())),
                value = it
            )
        }
    )

    private fun getWhatsappWidget(member: Member) = DropdownButton(
        name = "value",
        value = member.whatsapp.toString(),
        children = listOf(
            DropdownMenuItem(
                caption = getText("button.yes"),
                value = "true"
            ),
            DropdownMenuItem(
                caption = getText("button.no"),
                value = "false"
            )
        )
    )

    private fun getTimezoneWidget(member: Member) = SearchableDropdown(
        name = "value",
        value = member.timezoneId,
        children = TimeZone.getAvailableIDs()
            .filter { it.contains("/") }
            .map {
                DropdownMenuItem(it, it)
            }.sortedBy { it.caption }
    )

    private fun getCityWidget(member: Member) = SearchableDropdown(
        name = "value",
        value = member.city?.id?.toString(),
        required = if (member.business) true else null,
        children = membershipManagerApi.searchPlace(
            request = SearchPlaceRequest(
                country = member.country,
                type = "CITY",
                limit = 200
            )
        ).places
            .sortedBy { it.name }
            .map {
                DropdownMenuItem(
                    caption = it.name,
                    value = it.id.toString()
                )
            }
    )

    private fun getCategoryWidget(member: Member) = SearchableDropdown(
        name = "value",
        value = member.category?.id?.toString(),
        children = membershipManagerApi.searchCategory(
            request = SearchCategoryRequest(
                limit = 2000
            )
        ).categories
            .sortedBy { StringUtil.unaccent(it.title.uppercase()) }
            .map {
                DropdownMenuItem(
                    caption = it.title,
                    value = it.id.toString()
                )
            },
        required = true
    )

    private fun getInputWidget(
        value: String?,
        maxlength: Int,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false
    ) =
        Input(
            name = "value",
            value = value,
            type = type,
            maxLength = maxlength,
            maxLines = maxLines,
            required = required
        )

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, getLocale())
}
