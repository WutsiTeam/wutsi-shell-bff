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
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.TimeZone

@Service
class ProfileEditorWidgetProvider(
    private val regulationEngine: RegulationEngine,
    private val membershipManagerApi: MembershipManagerApi
) {
    fun get(name: String, member: Member): WidgetAware =
        when (name) {
            "display-name" -> getDisplayNameWidget(member)
            "email" -> getEmailWidget(member)
            "language" -> getLanguageWidget(member)
            "timezone-id" -> getTimezoneWidget(member)
            "city-id" -> getCityWidget(member)
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun getDisplayNameWidget(member: Member) = Input(
        name = "value",
        value = member.displayName,
        maxLength = 50,
        required = true
    )

    private fun getEmailWidget(member: Member) = Input(
        name = "value",
        value = member.email,
        maxLength = 160,
        type = InputType.Email
    )

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

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()
}
