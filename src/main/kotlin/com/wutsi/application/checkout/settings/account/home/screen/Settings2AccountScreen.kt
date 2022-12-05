package com.wutsi.application.checkout.settings.account.home.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.shared.Theme
import com.wutsi.application.util.SecurityUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.SearchPaymentMethodRequest
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/account")
class Settings2AccountScreen(
    private val membershipManagerApi: MembershipManagerApi,
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        val memberId = SecurityUtil.getMemberId()
        val member = membershipManagerApi.getMember(memberId).member
        return Screen(
            id = Page.SETTINGS_ACCOUNT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.app-bar.title")
            ),
            child = Column(
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOfNotNull(
                    toBalanceWidget(member),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = Container(
                            alignment = Alignment.TopCenter,
                            child = toAccountListWidget()
                        )
                    )
                )
            )
        ).toWidget()
    }

    private fun toBalanceWidget(member: Member): WidgetAware? {
        member.businessId ?: return null
        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val country = regulationEngine.country(business.country)

        return Column(
            children = listOfNotNull(
                Container(
                    alignment = Center,
                    padding = 10.0,
                    child = MoneyText(
                        value = business.balance.toDouble(),
                        currency = country.currencySymbol,
                        color = Theme.COLOR_PRIMARY,
                        numberFormat = country.numberFormat
                    )
                )
            ),
            mainAxisAlignment = MainAxisAlignment.center,
            crossAxisAlignment = CrossAxisAlignment.center,
            mainAxisSize = MainAxisSize.min
        )
    }

    private fun toAccountListWidget(): WidgetAware {
        val paymentMethods = checkoutManagerApi.searchPaymentMethod(
            request = SearchPaymentMethodRequest(
                status = PaymentMethodStatus.ACTIVE.name,
                limit = 100
            )
        ).paymentMethods

        val children = mutableListOf<WidgetAware>()
        children.addAll(
            paymentMethods
                .map {
                    ListItem(
                        caption = it.number,
                        iconLeft = it.provider.logoUrl,
//                        iconRight = Theme.ICON_CHEVRON_RIGHT,
                        padding = 10.0
//                        action = gotoUrl(
//                            url = urlBuilder.build("${Page.getSettingsUrl()}/account/profile?token=${it.token}")
//                        )
                    )
                }
        )
//        children.add(
//            Container(
//                padding = 10.0,
//                alignment = Center,
//                child = Button(
//                    caption = getText("page.settings.account.button.add-account"),
//                    action = gotoUrl(
//                        url = urlBuilder.build("${Page.getSettingsUrl()}/account/add")
//                    )
//                )
//            )
//        )
        return ListView(
            children = children,
            separatorColor = Theme.COLOR_DIVIDER,
            separator = true
        )
    }
}
