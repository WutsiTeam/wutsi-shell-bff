package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/category")
class SettingsBusinessCategoryPage(
    private val accountApi: WutsiAccountApi
) : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "category-id"
    override fun getPageIndex() = 3

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
