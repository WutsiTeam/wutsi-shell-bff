package com.wutsi.application.common.page

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon

abstract class AbstractSuccessPageEndpoint : AbstractPageEndpoint() {
    abstract fun getButton(): Button?

    override fun showHeader() = false

    override fun getBody() = Container(
        padding = 10.0,
        child = getButton()
    )

    override fun getIcon() = Icon(
        code = Theme.ICON_CHECK,
        color = Theme.COLOR_SUCCESS,
        size = 80.0
    )
}
