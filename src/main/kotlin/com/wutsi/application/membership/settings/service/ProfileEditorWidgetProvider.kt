package com.wutsi.application.membership.settings.service

import com.wutsi.application.util.StringUtil
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
            "biography" -> get(name, member.biography)
            "category-id" -> get(name, member.id)
            "city-id" -> get(name, member.city?.id, member.country)
            "display-name" -> get(name, member.displayName)
            "email" -> get(name, member.email)
            "facebook-id" -> get(name, member.facebookId)
            "instagram-id" -> get(name, member.instagramId)
            "language" -> get(name, member.language)
            "timezone-id" -> get(name, member.timezoneId)
            "twitter-id" -> get(name, member.twitterId)
            "whatsapp" -> get(name, member.whatsapp)
            "website" -> get(name, member.website)
            "youtube-id" -> get(name, member.youtubeId)
            else -> throw IllegalStateException("Not supported: $name")
        }

    fun get(name: String, defaultValue: Any?, country: String? = null): WidgetAware =
        when (name) {
            "biography" -> getInputWidget(defaultValue, 160, maxLines = 2)
            "category-id" -> getCategoryWidget(defaultValue?.toString()?.toLong())
            "city-id" -> getCityWidget(defaultValue?.toString()?.toLong(), country)
            "display-name" -> getInputWidget(defaultValue, 50, required = true)
            "email" -> getInputWidget(defaultValue, 160, InputType.Email)
            "facebook-id" -> getInputWidget(defaultValue, 30)
            "instagram-id" -> getInputWidget(defaultValue, 30)
            "language" -> getLanguageWidget(defaultValue?.toString())
            "timezone-id" -> getTimezoneWidget(defaultValue?.toString())
            "twitter-id" -> getInputWidget(defaultValue, 30)
            "whatsapp" -> getWhatsappWidget(defaultValue?.toString()?.toBoolean())
            "website" -> getInputWidget(defaultValue, 160, InputType.Url)
            "youtube-id" -> getInputWidget(defaultValue, 30)
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun getLanguageWidget(language: String?) = DropdownButton(
        name = "value",
        value = language,
        required = true,
        children = regulationEngine.supportedLanguages().map {
            DropdownMenuItem(
                caption = StringUtil.capitalizeFirstLetter(Locale(it).getDisplayLanguage(getLocale())),
                value = it
            )
        }
    )

    private fun getWhatsappWidget(value: Boolean?) = DropdownButton(
        name = "value",
        value = value?.toString(),
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

    private fun getTimezoneWidget(timezoneId: String?) = SearchableDropdown(
        name = "value",
        value = timezoneId,
        children = TimeZone.getAvailableIDs()
            .filter { it.contains("/") }
            .map {
                DropdownMenuItem(it, it)
            }.sortedBy { it.caption }
    )

    private fun getCityWidget(cityId: Long?, country: String?) = SearchableDropdown(
        name = "value",
        value = cityId?.toString(),
        required = true,
        children = membershipManagerApi.searchPlace(
            request = SearchPlaceRequest(
                country = country,
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

    private fun getCategoryWidget(categoryId: Long?) = SearchableDropdown(
        name = "value",
        value = categoryId?.toString(),
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
        value: Any?,
        maxlength: Int,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false
    ) =
        Input(
            name = "value",
            value = value?.toString(),
            type = type,
            maxLength = maxlength,
            maxLines = maxLines,
            required = required
        )

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, getLocale())
}
