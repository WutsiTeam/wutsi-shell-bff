package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment

class PictureWidget(
    private val url: String,
    private val width: Double = 150.0,
    private val height: Double = 150.0,
    private val padding: Double? = null,
    private val action: Action? = null
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware =
        Container(
            padding = padding,
            width = width,
            height = height,
            alignment = Alignment.Center,
            borderColor = Theme.COLOR_PRIMARY_LIGHT,
            border = 1.0,
            backgroundImageUrl = url,
            action = action
        )
}
