package com.wutsi.application.membership.onboard.page

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.membership.onboard.dao.OnboardRepository
import com.wutsi.application.membership.onboard.exception.OnboardEntityNotFoundException
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractOnboardPage : AbstractEndpoint() {
    @Autowired
    protected lateinit var onboardDao: OnboardRepository

    @Value("\${wutsi.application.asset-url}")
    protected lateinit var assertUrl: String

    fun getLogoUrl() = "$assertUrl/logo/wutsi.png"

    fun gotoLogin(
        phoneNumber: String,
        title: String? = null,
        subTitle: String? = null
    ): Action {
        return gotoUrl(
            url = urlBuilder.build(
                Page.getLoginUrl() + "?title=" + encodeURLParam(title ?: "") +
                    "&sub-title=" + encodeURLParam(subTitle ?: getText("page.login.sub-title")) +
                    "&phone=" + encodeURLParam(phoneNumber) +
                    "&return-to-route=true" +
                    "&return-url=" + encodeURLParam("route:/") +
                    "&hide-change-account-button=true"
            ),
            type = ActionType.Route,
            replacement = true
        )
    }

    @ExceptionHandler(OnboardEntityNotFoundException::class)
    fun onOnboardEntityNotFoundException(ex: OnboardEntityNotFoundException) =
        gotoUrl("/onboard/2", ActionType.Route)
}
