package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.regulation.Country
import java.text.DecimalFormat

class KpiWidget(
    private val name: String,
    private val value: Long,
    private val country: Country,
    private val money: Boolean = false,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware {
        return Container(
            background = Theme.COLOR_WHITE,
            borderColor = Theme.COLOR_DIVIDER,
            borderRadius = 10.0,
            padding = 10.0,
            margin = 10.0,
            border = 1.0,
            child = Column(
                children = listOf(
                    if (money) {
                        MoneyText(
                            numberFormat = country.numberFormat,
                            currency = country.currencySymbol,
                            color = Theme.COLOR_PRIMARY,
                            valueFontSize = Theme.TEXT_SIZE_X_LARGE,
                            bold = true,
                            value = value.toDouble(),
                        )
                    } else {
                        Text(
                            caption = DecimalFormat(country.numberFormat).format(value),
                            size = Theme.TEXT_SIZE_X_LARGE,
                            color = Theme.COLOR_PRIMARY,
                            bold = true,
                        )
                    },
                    Container(padding = 10.0),
                    Text(name),
                ),
            ),
        )
    }
}
