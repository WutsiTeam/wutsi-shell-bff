package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.CityService
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/city")
class SettingsProfileCityScreen(
    private val cityService: CityService,
    private val tenantProvider: TenantProvider
) : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "city-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_CITY

    override fun getInputWidget(account: Account): WidgetAware {
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()

        return SearchableDropdown(
            name = "value",
            value = user.cityId?.toString(),
            children = cityService.search(null, tenant.countries)
                .sortedBy { sharedUIMapper.toLocationText(it, user.country) }
                .map {
                    DropdownMenuItem(
                        caption = sharedUIMapper.toLocationText(it, user.country),
                        value = it.id.toString()
                    )
                }
        )
    }
}
