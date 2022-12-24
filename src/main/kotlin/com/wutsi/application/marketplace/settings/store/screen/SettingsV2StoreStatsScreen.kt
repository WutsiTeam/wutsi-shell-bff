package com.wutsi.application.marketplace.settings.store.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.widget.KpiWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.SearchSalesKpiRequest
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store/stats")
class SettingsV2StoreStatsScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val business = member.businessId?.let { checkoutManagerApi.getBusiness(it).business }

        return Screen(
            id = Page.SETTINGS_STORE_STATS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.store.stats.app-bar.title"),
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = listOfNotNull(
                        toOverallKpiWidget(member, business),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toOverallKpiWidget(member: Member, business: Business?): WidgetAware? {
        val salesKpis = business?.let {
            checkoutManagerApi.searchSalesKpi(
                request = SearchSalesKpiRequest(
                    aggregate = true,
                    businessId = member.businessId,
                ),
            ).kpis
        }
        if (salesKpis == null || salesKpis.isEmpty()) {
            return null
        }

        val country = regulationEngine.country(member.country)
        val kpi = salesKpis[0]
        return Container(
            padding = 10.0,
            child = Row(
                mainAxisAlignment = MainAxisAlignment.spaceEvenly,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    Flexible(
                        child = KpiWidget(
                            name = getText("page.settings.store.stats.orders"),
                            value = kpi.totalOrders,
                            country = country,
                        ),
                    ),
                    Flexible(
                        child = KpiWidget(
                            name = getText("page.settings.store.stats.sales"),
                            value = kpi.totalValue,
                            country = country,
                            money = true,
                        ),
                    ),
                ),
            ),
        )
    }
}
