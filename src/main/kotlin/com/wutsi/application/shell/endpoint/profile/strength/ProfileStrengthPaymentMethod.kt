package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthPaymentMethod(
    private val accountApi: WutsiAccountApi,
    private val togglesProvider: TogglesProvider,
) : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        try {
            togglesProvider.isAccountEnabled() && accountApi.listPaymentMethods(account.id).paymentMethods.isEmpty()
        } catch (ex: Exception) {
            false
        }

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_PAYMENT, size = size, color = Theme.COLOR_PRIMARY)

    override fun getTitle() = getText("profile-strength.payment-method.title")
    override fun getDescription() = getText("profile-strength.payment-method.description")
    override fun getActionTitle() = getText("profile-strength.payment-method.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build("/settings/accounts/link")
    )
}
