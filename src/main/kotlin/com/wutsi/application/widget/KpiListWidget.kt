package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.SalesKpiSummary
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.regulation.Country

class KpiListWidget(
    private val kpis: List<KpiWidget>,
) : CompositeWidgetAware() {
    companion object {
        fun of(business: Business, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = business.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = business.totalSales,
                        country = country,
                        money = true,
                    ),
                ),
            )

        fun of(business: Business, kpi: SalesKpiSummary, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = kpi.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = kpi.totalValue,
                        country = country,
                        money = true,
                    ),
                ),
            )

        fun of(product: Product, kpi: SalesKpiSummary, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = kpi.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.units"),
                        value = kpi.totalUnits,
                        country = country,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = kpi.totalValue,
                        country = country,
                        money = true,
                    ),
                ),
            )
    }

    override fun toWidgetAware(): WidgetAware {
        return Container(
            background = Theme.COLOR_WHITE,
            child = Column(
                children = kpis.flatMap {
                    listOf(
                        it,
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                    )
                },
            ),
        )
    }
}
