package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TranslationUtil.getText
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.BottomNavigationBar
import com.wutsi.flutter.sdui.BottomNavigationBarItem
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType

class BottomNavigationBarWidget(
    private val profileUrl: String? = null,
    private val chatUrl: String? = null,
    private val ordersUrl: String? = null
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware = toBottomNavigationBar()

    fun toBottomNavigationBar() = BottomNavigationBar(
        background = Theme.COLOR_PRIMARY,
        selectedItemColor = Theme.COLOR_WHITE,
        unselectedItemColor = Theme.COLOR_WHITE,
        items = listOfNotNull(
            BottomNavigationBarItem(
                icon = Theme.ICON_HOME,
                caption = getText("share-ui.bottom-nav-bar.home"),
                action = Action(
                    type = ActionType.Route,
                    url = "route:/~"
                )
            ),
            profileUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_PERSON,
                    caption = getText("share-ui.bottom-nav-bar.me"),
                    action = Action(
                        type = ActionType.Route,
                        url = it
                    )
                )
            },
            chatUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_CHAT,
                    caption = getText("share-ui.bottom-nav-bar.chat"),
                    action = Action(
                        type = ActionType.Route,
                        url = it
                    )
                )
            },
            ordersUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_ORDER,
                    caption = getText("share-ui.bottom-nav-bar.orders"),
                    action = Action(
                        type = ActionType.Route,
                        url = it
                    )
                )
            }

//            model.transactionUrl?.let {
//                BottomNavigationBarItem(
//                    icon = Theme.ICON_HISTORY,
//                    caption = getText("share-ui.bottom-nav-bar.transactions"),
//                    action = Action(
//                        type = ActionType.Route,
//                        url = it
//                    )
//                )
//            },
        )
    )
}
