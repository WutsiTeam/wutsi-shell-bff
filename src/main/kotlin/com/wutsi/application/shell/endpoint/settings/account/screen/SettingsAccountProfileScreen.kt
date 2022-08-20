package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.payment.PaymentMethodType
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/account/profile")
class SettingsAccountProfileScreen(
    private val tenantProvider: TenantProvider,
    private val accountApi: WutsiAccountApi,
    private val accountService: AccountService
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam token: String): Widget {
        val tenant = tenantProvider.get()
        val paymentMethod = accountApi.getPaymentMethod(securityContext.currentAccountId(), token).paymentMethod
        val logoUrl = accountService.getLogoUrl(tenant, paymentMethod)

        return Screen(
            id = Page.SETTINGS_ACCOUNT_PROFILE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.profile.app-bar.title")
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = listOf(
                        toRowWidget(
                            "page.settings.account.profile.provider",
                            Row(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.center,
                                children = listOfNotNull(
                                    logoUrl?.let {
                                        Image(
                                            width = 32.0,
                                            height = 32.0,
                                            url = it
                                        )
                                    },
                                    Container(padding = 5.0),
                                    Text(
                                        caption = when (paymentMethod.type) {
                                            PaymentMethodType.MOBILE.name -> accountService.findMobileCarrier(
                                                tenant,
                                                paymentMethod.provider
                                            )?.name ?: ""
                                            PaymentMethodType.BANK.name -> accountService.findFinancialInstitution(
                                                tenant,
                                                paymentMethod.provider
                                            )?.name ?: ""
                                            else -> ""
                                        }
                                    )
                                )
                            )
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        toRowWidget(
                            key = "page.settings.account.profile.country",
                            value = Locale(
                                LocaleContextHolder.getLocale().language,
                                paymentMethod.phone?.country ?: paymentMethod.bankAccount?.country ?: ""
                            ).displayCountry
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        toRowWidget(
                            key = "page.settings.account.profile.number",
                            value = formattedAccountNumber(paymentMethod)
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        toRowWidget(
                            key = "page.settings.account.profile.owner",
                            value = paymentMethod.ownerName
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                    )
                ),
            ),
        ).toWidget()
    }

    private fun toRowWidget(key: String, value: String?): WidgetAware =
        toRowWidget(key, Text(value ?: ""))

    private fun toRowWidget(key: String, value: WidgetAware): WidgetAware =
        Row(
            children = listOf(
                Flexible(
                    flex = 2,
                    child = Container(
                        padding = 10.0,
                        child = Text(
                            getText(key),
                            bold = true,
                            alignment = TextAlignment.Right,
                        )
                    ),
                ),
                Flexible(
                    flex = 3,
                    child = value,
                )
            )
        )
}
