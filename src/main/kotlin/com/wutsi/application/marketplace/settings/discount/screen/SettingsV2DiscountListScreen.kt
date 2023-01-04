package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.DiscountSummary
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/2/discounts/list")
class SettingsV2DiscountListScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val discounts = marketplaceManagerApi.searchDiscount(
            request = SearchDiscountRequest(
                storeId = member.storeId ?: -1,
                limit = 100,
            ),
        ).discounts
        val country = regulationEngine.country(member.country)

        return Screen(
            id = Page.SETTINGS_DISCOUNT_LIST,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.discounts.app-bar.title"),
            ),
            floatingActionButton = Button(
                type = ButtonType.Floatable,
                icon = Theme.ICON_ADD,
                stretched = false,
                iconColor = Theme.COLOR_WHITE,
                action = gotoUrl(
                    url = urlBuilder.build(Page.getSettingsDiscountAddUrl()),
                ),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.discounts.count", arrayOf(discounts.size)),
                            alignment = TextAlignment.Center,
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                    Flexible(
                        child = ListView(
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = discounts.map { toListItem(it, country, member) },
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toListItem(discount: DiscountSummary, country: Country, member: Member) = ListItem(
        caption = discount.name,
        trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
        subCaption = toDuration(discount, country, member),
        action = gotoUrl(
            url = urlBuilder.build(Page.getSettingsDiscountUrl()),
            parameters = mapOf("id" to discount.id.toString()),
        ),
    )

    private fun toDuration(discount: DiscountSummary, country: Country, member: Member): String? {
        return if (discount.starts != null && discount.ends != null) {
            DateTimeUtil.convert(discount.starts!!, member.timezoneId)
                .format(DateTimeFormatter.ofPattern(country.dateFormatShort)) +
                " - " +
                DateTimeUtil.convert(discount.ends!!, member.timezoneId)
                    .format(DateTimeFormatter.ofPattern(country.dateFormat))
        } else if (discount.starts != null) {
            DateTimeUtil.convert(discount.starts!!, member.timezoneId)
                .format(DateTimeFormatter.ofPattern(country.dateFormat))
        } else {
            null
        }
    }
}
