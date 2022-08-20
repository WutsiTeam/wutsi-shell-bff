package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/category")
class SettingsProfileCategoryScreen(
    private val accountApi: WutsiAccountApi
) : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "category-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_CATEGORY

    override fun getInputWidget(account: Account): WidgetAware {
        val user = securityContext.currentAccount()
        return SearchableDropdown(
            name = "value",
            value = user.category?.id?.toString(),
            children = accountApi.listCategories().categories
                .sortedBy { it.title }
                .map {
                    DropdownMenuItem(
                        caption = it.title,
                        value = it.id.toString()
                    )
                },
            required = true
        )
    }
}
