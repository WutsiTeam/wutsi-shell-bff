package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.regulation.Country

class ProductListItemWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val summary: String? = null,
    private val pictureUrl: String? = null,
    private val action: Action? = null
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware = ListItem(
        caption = title,
        subCaption = summary,
        padding = 10.0,
        leading = pictureUrl?.let { Image(it, width = 48.0, height = 48.0) },
        trailing = if (price != null)
            MoneyText(
                color = Theme.COLOR_PRIMARY,
                value = price.toDouble(),
                currency = country.currencySymbol,
                numberFormat = country.monetaryFormat,
                valueFontSize = Theme.TEXT_SIZE_LARGE,
                currencyFontSize = Theme.TEXT_SIZE_SMALL
            )
        else
            null,
        action = action
    )
}
