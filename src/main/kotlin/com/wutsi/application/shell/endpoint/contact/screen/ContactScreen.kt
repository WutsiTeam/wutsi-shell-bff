package com.wutsi.application.shell.endpoint.contact.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.ProfileListItem
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class ContactScreen(
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val contacts = contactApi.searchContact(
            SearchContactRequest(
                limit = 1000,
                offset = 0
            )
        ).contacts

        val accountIds = contacts.map { it.contactId }
        val accounts = if (accountIds.isEmpty())
            emptyList()
        else
            accountApi.searchAccount(
                SearchAccountRequest(
                    status = AccountStatus.ACTIVE.name,
                    ids = accountIds,
                    limit = accountIds.size
                )
            ).accounts.sortedBy { it.displayName }

        return Screen(
            id = Page.CONTACTS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.contact.app-bar.title")
            ),
            child = ListView(
                separatorColor = Theme.COLOR_DIVIDER,
                separator = true,
                children = accounts.map {
                    ProfileListItem(
                        showAccountType = true,
                        model = sharedUIMapper.toAccountModel(it),
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build("profile?id=${it.id}")
                        )
                    )
                }
            ),
            bottomNavigationBar = bottomNavigationBar()
        ).toWidget()
    }
}
