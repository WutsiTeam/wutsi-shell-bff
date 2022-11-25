package com.wutsi.application.home

import com.wutsi.application.AbstractSecuredEndpoint
import com.wutsi.application.Page
import com.wutsi.application.membership.onboard.screen.OnboardV2Screen
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/2")
class HomeV2Screen(
    private val onboard: OnboardV2Screen
) : AbstractSecuredEndpoint() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeV2Screen::class.java)
    }

    @PostMapping
    fun index(): Widget {
        try {
            val member = getCurrentMember()
            return Screen(
                id = Page.HOME,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_GRAY_LIGHT,
                    foregroundColor = Theme.COLOR_BLACK,
                    actions = listOf(
                        IconButton(
                            icon = Theme.ICON_SETTINGS,
                            action = Action(
                                type = ActionType.Route,
                                url = urlBuilder.build(Page.getSettingsUrl())
                            )
                        )
                    ),
                    automaticallyImplyLeading = false,
                    title = member.displayName
                ),
                bottomNavigationBar = createBottomNavigationBarWidget(),
                backgroundColor = Theme.COLOR_GRAY_LIGHT
            ).toWidget()
        } catch (ex: FeignException.NotFound) {
            LOGGER.warn("Unable to resolve current member", ex)

            logger.add("member_not_found", true)
            return onboard.index()
        }
    }
}
