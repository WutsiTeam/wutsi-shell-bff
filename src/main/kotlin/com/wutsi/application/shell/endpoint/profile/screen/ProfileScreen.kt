package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.PhoneUtil
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.ui.BottomNavigationButton
import com.wutsi.application.shared.ui.CartIcon
import com.wutsi.application.shared.ui.ProfileCard
import com.wutsi.application.shared.ui.ProfileCardType
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.ecommerce.cart.WutsiCartApi
import com.wutsi.ecommerce.cart.dto.Cart
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.DynamicWidget
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.tenant.dto.Tenant
import com.wutsi.platform.tenant.entity.ToggleName
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileScreen(
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi,
    private val cartApi: WutsiCartApi,
    private val tenantProvider: TenantProvider,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String
) : AbstractQuery() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProfileScreen::class.java)
    }

    @PostMapping
    fun index(
        @RequestParam(required = false) id: Long? = null,
        @RequestParam(required = false) tab: String? = null
    ): Widget {
        val user = accountApi.getAccount(
            id ?: securityContext.currentAccountId()
        ).account
        val active = AccountStatus.ACTIVE.name.equals(user.status, true)
        val qrEnabled = togglesProvider.isToggleEnabled(ToggleName.SCAN)
        val tenant = tenantProvider.get()
        val cart = getCart(user)

        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.profile.tab.about").uppercase(), bold = true),

                if (active && isStoreEnabled(user))
                    Text(getText("page.profile.tab.store").uppercase(), bold = true)
                else
                    null,

                if (active && qrEnabled)
                    Text(getText("page.profile.tab.qr-code").uppercase(), bold = true)
                else
                    null
            )
        )
        val tabViews = TabBarView(
            children = listOfNotNull(
                aboutTab(user),

                if (active && isStoreEnabled(user))
                    storeTab(user)
                else
                    null,

                if (active && qrEnabled)
                    qrCodeTab(user)
                else
                    null
            )
        )

        return if (tabViews.children.size == 1)
            Screen(
                id = Page.PROFILE,
                backgroundColor = Theme.COLOR_WHITE,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_PRIMARY,
                    foregroundColor = Theme.COLOR_WHITE,
                    actions = titleBarActions(user, cart, tenant)
                ),
                child = tabViews.children[0],
                bottomNavigationBar = bottomNavigationBar(BottomNavigationButton.PROFILE)
            ).toWidget()
        else
            DefaultTabController(
                id = Page.PROFILE,
                length = tabs.tabs.size,
                initialIndex = if (tab == "store" && isStoreEnabled(user))
                    1
                else
                    null,
                child = Screen(
                    backgroundColor = Theme.COLOR_WHITE,
                    appBar = AppBar(
                        elevation = 0.0,
                        backgroundColor = Theme.COLOR_PRIMARY,
                        foregroundColor = Theme.COLOR_WHITE,
                        actions = titleBarActions(user, cart, tenant),
                        bottom = tabs
                    ),
                    child = tabViews,
                    bottomNavigationBar = bottomNavigationBar()
                )
            ).toWidget()
    }

    private fun titleBarActions(user: Account, cart: Cart?, tenant: Tenant): List<WidgetAware>? =
        if (AccountStatus.ACTIVE.name != user.status)
            null
        else
            listOfNotNull(
                if (user.business)
                    if (togglesProvider.isToggleEnabled(ToggleName.CHAT)) {
                        Container(
                            padding = 4.0,
                            child = CircleAvatar(
                                radius = 20.0,
                                backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                child = IconButton(
                                    icon = Theme.ICON_CHAT,
                                    size = 20.0,
                                    action = Action(
                                        type = ActionType.Route,
                                        url = "$chatUrl/messages?recipient-id=${user.id}"
                                    )
                                )
                            )
                        )
                    } else {
                        PhoneUtil.toWhatsAppUrl(user.whatsapp)?.let {
                            Container(
                                padding = 4.0,
                                child = CircleAvatar(
                                    radius = 20.0,
                                    backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                    child = IconButton(
                                        icon = Theme.ICON_CHAT,
                                        size = 20.0,
                                        action = Action(
                                            type = ActionType.Navigate,
                                            url = it
                                        )
                                    )
                                )
                            )
                        }
                    }
                else
                    null,

                if (togglesProvider.isContactEnabled() && canAddContact(user))
                    Container(
                        padding = 4.0,
                        child = CircleAvatar(
                            radius = 20.0,
                            backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                            child = IconButton(
                                icon = Theme.ICON_ADD_PERSON,
                                size = 20.0,
                                action = Action(
                                    type = ActionType.Command,
                                    url = urlBuilder.build("commands/add-contact?contact-id=${user.id}"),
                                    prompt = Dialog(
                                        type = DialogType.Confirm,
                                        title = getText("prompt.confirm.title"),
                                        message = getText(
                                            "page.profile.confirm-add-contact",
                                            arrayOf(user.displayName ?: "")
                                        )
                                    ).toWidget()
                                )
                            )
                        )
                    )
                else
                    null,

                Container(
                    padding = 4.0,
                    child = CircleAvatar(
                        radius = 20.0,
                        backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                        child = IconButton(
                            icon = Theme.ICON_SHARE,
                            size = 20.0,
                            action = Action(
                                type = ActionType.Share,
                                url = "${tenant.webappUrl}/profile?id=${user.id}"
                            )
                        )
                    )
                ),

                if (cart != null && cart.products.isNotEmpty())
                    Container(
                        padding = 4.0,
                        child = CircleAvatar(
                            radius = 20.0,
                            backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                            child = CartIcon(
                                productCount = cart.products.size,
                                size = 20.0,
                                action = Action(
                                    type = ActionType.Route,
                                    url = urlBuilder.build(storeUrl, "cart?merchant-id=${user.id}")
                                )
                            )
                        )
                    )
                else
                    null
            )

    private fun isStoreEnabled(user: Account): Boolean =
        user.business && togglesProvider.isStoreEnabled() && user.hasStore

    private fun aboutTab(user: Account): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            ProfileCard(
                model = sharedUIMapper.toAccountModel(user),
                type = ProfileCardType.FULL,
                assetUrl = assetUrl
            )
        )
        return Column(
            children = children,
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start
        )
    }

    private fun storeTab(user: Account) = DynamicWidget(
        url = urlBuilder.build(storeUrl, "store/widget?id=${user.id}")
    )

    private fun qrCodeTab(user: Account) = DynamicWidget(
        url = urlBuilder.build("profile/qr-code-widget?id=${user.id}")
    )

    private fun canAddContact(user: Account): Boolean =
        if (user.id == securityContext.currentAccountId())
            false
        else
            contactApi.searchContact(
                request = SearchContactRequest(
                    contactIds = listOf(user.id)
                )
            ).contacts.isEmpty()

    private fun getCart(merchant: Account): Cart? =
        if (
            AccountStatus.ACTIVE.name.equals(merchant.status, true) &&
            merchant.business &&
            togglesProvider.isCartEnabled() &&
            merchant.hasStore
        )
            try {
                cartApi.getCart(merchant.id).cart
            } catch (ex: Exception) {
                LOGGER.warn("Unable to resolve the Cart for Merchant #${merchant.id}", ex)
                null
            }
        else
            null
}
