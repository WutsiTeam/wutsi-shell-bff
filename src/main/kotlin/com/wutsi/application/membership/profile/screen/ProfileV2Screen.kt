package com.wutsi.application.membership.profile.screen

import com.wutsi.application.AbstractSecuredEndpoint
import com.wutsi.application.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.BusinessToolbarWidget
import com.wutsi.application.widget.GridWidget
import com.wutsi.application.widget.OfferWidget
import com.wutsi.application.widget.ProfileWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.DynamicWidget
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.image.ImageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile/2")
class ProfileV2Screen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(
        @RequestParam(required = false) id: Long? = null,
        @RequestParam tab: String? = null
    ): Widget {
        val member = membershipManagerApi.getMember(
            id ?: SecurityUtil.getMemberId()
        ).member
        val store = hasStore(member)
        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.profile.tab.about").uppercase(), bold = true),

                if (store) {
                    Text(getText("page.profile.tab.store").uppercase(), bold = true)
                } else {
                    null
                }
            )
        )
        val tabViews = TabBarView(
            children = listOfNotNull(
                toAboutTab(member),
                if (store) {
                    toStoreTab(member)
                } else {
                    null
                }
            )
        )

        return if (tabViews.children.size == 1) {
            Screen(
                id = Page.PROFILE,
                backgroundColor = Theme.COLOR_WHITE,
                appBar = toAppBar(member, null),
                child = tabViews.children[0],
                bottomNavigationBar = createBottomNavigationBarWidget()
            ).toWidget()
        } else {
            DefaultTabController(
                id = Page.PROFILE,
                length = tabs.tabs.size,
                initialIndex = if (tab?.lowercase() == "store" && store) {
                    1
                } else {
                    null
                },
                child = Screen(
                    backgroundColor = Theme.COLOR_WHITE,
                    appBar = toAppBar(member, tabs),
                    child = tabViews,
                    bottomNavigationBar = createBottomNavigationBarWidget()
                )
            ).toWidget()
        }
    }

    private fun toAppBar(member: Member, tabs: TabBar?) = AppBar(
        elevation = 0.0,
        backgroundColor = Theme.COLOR_PRIMARY,
        foregroundColor = Theme.COLOR_WHITE,
        bottom = tabs,
        title = member.displayName,
        actions = listOf(
            IconButton(
                icon = Theme.ICON_SETTINGS,
                action = Action(
                    type = ActionType.Route,
                    url = urlBuilder.build(Page.getSettingsUrl())
                )
            )
        )
    )

    private fun toAboutTab(member: Member): WidgetAware {
        val children = mutableListOf<WidgetAware>()
        children.add(ProfileWidget.of(member))
        if (member.business) {
            children.addAll(
                listOfNotNull(
                    Divider(color = Theme.COLOR_DIVIDER),
                    BusinessToolbarWidget.of(
                        member = member,
                        storeAction = gotoStore(member),
                        webappUrl = webappUrl
                    ),
                    toOffersWidget(member),
                    toSocialWidget(member)
                )
            )
        }
        return SingleChildScrollView(
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = children
            )
        )
    }

    private fun toStoreTab(member: Member) = DynamicWidget(
        url = urlBuilder.build("${Page.getCatalogUrl()}/fragment?id=${member.id}")
    )

    private fun toOffersWidget(member: Member): WidgetAware? {
        if (member.storeId == null) {
            return null
        }

        val offers = marketplaceManagerApi.searchProduct(
            request = SearchProductRequest(
                storeId = member.storeId,
                limit = 2,
                status = "PUBLISHED"
            )
        ).products
        if (offers.isEmpty()) {
            return null
        }

        val country = regulationEngine.country(member.country)
        return Column(
            children = listOf(
                Divider(color = Theme.COLOR_DIVIDER),
                GridWidget(
                    children = offers
                        .map { OfferWidget.of(it, country, gotoOffer(it), imageService) },
                    columns = 2
                ),
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.profile.button.more-product"),
                        action = gotoStore(member)
                    )
                )
            )
        )
    }

    private fun toSocialWidget(member: Member): WidgetAware? {
        if (!member.business) {
            return null
        }
        val children = listOfNotNull(
            toSocialIcon(
                member.instagramId,
                "https://www.instagram.com/",
                "$assertUrl/images/social/instagram.png"
            ),
            toSocialIcon(member.youtubeId, "https://www.youtube.com/@", "$assertUrl/images/social/youtube.png"),
            toSocialIcon(member.facebookId, "https://www.facebook.com/", "$assertUrl/images/social/facebook.png"),
            toSocialIcon(member.twitterId, "https://www.twitter.com/", "$assertUrl/images/social/twitter.png"),
            toSocialIcon(member.website, "", "$assertUrl/images/social/website.png")
        )
        if (children.isEmpty()) {
            return null
        }
        return Column(
            children = listOf(
                Divider(color = Theme.COLOR_DIVIDER),
                Container(
                    padding = 10.0,
                    child = Row(
                        mainAxisAlignment = MainAxisAlignment.spaceEvenly,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = children
                    )
                )
            )
        )
    }

    private fun toSocialIcon(id: String?, urlPrefix: String, iconUrl: String): WidgetAware? =
        if (id.isNullOrEmpty()) {
            null
        } else {
            Container(
                child = Image(
                    width = 32.0,
                    height = 32.0,
                    url = iconUrl
                ),
                action = Action(
                    type = ActionType.Navigate,
                    url = "$urlPrefix$id"
                )
            )
        }

    private fun hasStore(user: Member): Boolean =
        user.active && user.business && (user.storeId != null)

    private fun gotoStore(member: Member) = gotoUrl(
        url = urlBuilder.build("${Page.getProfileUrl()}?id=${member.id}&tab=store"),
        replacement = true
    )

    private fun gotoOffer(product: ProductSummary) = gotoUrl(
        url = urlBuilder.build("${Page.getProductUrl()}?id=${product.id}")
    )
}
