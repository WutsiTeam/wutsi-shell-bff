package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/accounts/link")
class SettingsLinkAccountScreen(
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val financialInstitutions = tenant.financialInstitutions.sortedBy { it.name }
        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.link-account.app-bar.title")
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = listOfNotNull(
                        ListItem(
                            caption = getText("page.link-account.item.mobile"),
                            leading = Icon(code = Theme.ICON_MOBILE, color = Theme.COLOR_PRIMARY),
                            trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                            action = Action(
                                type = Route,
                                url = urlBuilder.build("settings/accounts/link/mobile")
                            )
                        ),
                        if (financialInstitutions.isNotEmpty())
                            ListItem(
                                caption = getText("page.link-account.item.bank"),
                                leading = Icon(code = Theme.ICON_BANK, color = Theme.COLOR_PRIMARY),
                                trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                                action = Action(
                                    type = Route,
                                    url = urlBuilder.build("settings/accounts/link/bank")
                                )
                            )
                        else
                            null,
                    )
                )
            )
        ).toWidget()
    }
}
