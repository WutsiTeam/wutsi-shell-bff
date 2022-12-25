package com.wutsi.application.util

import com.wutsi.checkout.manager.dto.SalesKpiSummary
import com.wutsi.flutter.sdui.ChartData
import java.time.LocalDate

object KpiUtil {
    fun toChartDataList(
        kpis: List<SalesKpiSummary>,
        from: LocalDate,
        to: LocalDate,
    ): List<ChartData> {
        val kpiMap = kpis.associateBy { it.date }
        val data = mutableListOf<ChartData>()

        var cur = from
        while (!cur.isAfter(to)) {
            data.add(
                ChartData(cur.toString(), kpiMap[cur]?.totalOrders?.toDouble() ?: 0.0),
            )
            cur = cur.plusDays(1)
        }
        return data
    }
}
