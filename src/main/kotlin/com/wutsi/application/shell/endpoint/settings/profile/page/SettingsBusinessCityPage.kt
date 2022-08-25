package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.application.shared.service.CityService
import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/city")
class SettingsBusinessCityPage(
    private val cityService: CityService,
    private val tenantProvider: TenantProvider
) : AbstractBusinessAttributePage() {
    override fun getPageIndex() = 4
    override fun getAttributeName() = "city-id"

    override fun getInputWidget(account: Account): WidgetAware {
        val tenant = tenantProvider.get()
        val user = securityContext.currentAccount()

        return SearchableDropdown(
            name = "value",
            value = user.cityId?.toString(),
            children = cityService.search(null, tenant.countries)
                .sortedBy {
                    StringUtil.unaccent(
                        sharedUIMapper.toLocationText(it, user.country).uppercase()
                    )
                }
                .map {
                    DropdownMenuItem(
                        caption = sharedUIMapper.toLocationText(it, user.country),
                        value = it.id.toString()
                    )
                }
        )
    }
}
