package com.wutsi.application.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.ui.CompositeWidgetAware
import com.wutsi.application.util.WhatsappUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.membership.manager.dto.Member

class BusinessToolbarWidget(
    private val phoneNumber: String? = null,
    private val storeId: Long? = null,
    private val storeAction: Action? = null,
    private val websiteUrl: String? = null,
    private val whatsapp: Boolean = false
) : CompositeWidgetAware() {
    companion object {
        fun of(member: Member, storeAction: Action?) = BusinessToolbarWidget(
            phoneNumber = member.phoneNumber,
            storeId = member.storeId,
            storeAction = storeAction,
            websiteUrl = member.website,
            whatsapp = member.whatsapp
        )
    }

    override fun toWidgetAware(): WidgetAware = Container(
        child = Row(
            mainAxisAlignment = MainAxisAlignment.spaceEvenly,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOfNotNull(
                if (storeId != null && storeAction != null) {
                    toIconButton(
                        icon = Theme.ICON_STORE,
                        caption = getText("widget.business-toolbar.shop"),
                        action = storeAction
                    )
                } else {
                    null
                },
                phoneNumber?.let {
                    toIconButton(
                        icon = Theme.ICON_PHONE,
                        caption = getText("widget.business-toolbar.call"),
                        action = Action(
                            type = ActionType.Navigate,
                            url = "tel:$phoneNumber"
                        )
                    )
                },
                if (whatsapp && phoneNumber != null) {
                    toIconButton(
                        icon = Theme.ICON_CHAT,
                        caption = getText("widget.business-toolbar.chat"),
                        action = Action(
                            type = ActionType.Navigate,
                            url = WhatsappUtil.url(phoneNumber)
                        )
                    )
                } else {
                    null
                },
                websiteUrl?.let {
                    toIconButton(
                        icon = Theme.ICON_LINK,
                        caption = getText("widget.business-toolbar.website"),
                        action = Action(
                            type = ActionType.Navigate,
                            url = it
                        )
                    )
                }
            )
        )
    )

    private fun toIconButton(icon: String, caption: String, action: Action?): WidgetAware {
        val radius = 24.0
        return Container(
            alignment = Alignment.Center,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.center,
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOfNotNull(
                    CircleAvatar(
                        backgroundColor = Theme.COLOR_PRIMARY,
                        radius = radius,
                        child = CircleAvatar(
                            backgroundColor = Theme.COLOR_WHITE,
                            radius = radius - 1,
                            child = Icon(
                                code = icon,
                                size = radius - 4.0
                            ),
                            action = action
                        )
                    ),
                    Container(
                        padding = 5.0,
                        child = Text(caption.uppercase(), color = Theme.COLOR_PRIMARY, bold = true),
                        action = action
                    )
                )
            )
        )
    }

    private fun getText(key: String, args: Array<Any> = emptyArray()): String =
        WidgetL10n.getText(key, args)
}
