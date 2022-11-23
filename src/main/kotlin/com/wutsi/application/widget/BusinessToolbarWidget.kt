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
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.membership.manager.dto.Member

class BusinessToolbarWidget(
    private val phoneNumber: String? = null,
    private val storeId: Long? = null,
    private val storeAction: Action? = null,
    private val whatsapp: Boolean = false,
    private val shareUrl: String? = null
) : CompositeWidgetAware() {
    companion object {
        fun of(member: Member, webappUrl: String, storeAction: Action?) = BusinessToolbarWidget(
            phoneNumber = member.phoneNumber,
            storeId = member.storeId,
            storeAction = storeAction,
            whatsapp = member.whatsapp,
            shareUrl = "$webappUrl/u/${member.id}"
        )

        fun of(product: Product, member: Member, webappUrl: String) = BusinessToolbarWidget(
            phoneNumber = member.phoneNumber,
            whatsapp = member.whatsapp,
            shareUrl = "$webappUrl/p/${product.id}"
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
                shareUrl?.let {
                    toIconButton(
                        icon = Theme.ICON_SHARE,
                        caption = getText("widget.business-toolbar.share"),
                        action = Action(
                            type = ActionType.Share,
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
