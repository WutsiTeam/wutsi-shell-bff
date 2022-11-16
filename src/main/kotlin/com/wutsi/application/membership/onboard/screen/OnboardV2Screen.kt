package com.wutsi.application.membership.onboard.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.flutter.sdui.Page
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class OnboardV2Screen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "onboard/pages/phone",
            "onboard/pages/verification",
            "onboard/pages/profile",
            "onboard/pages/pin",
            "onboard/pages/confirm-pin",
            "onboard/pages/final",
            "onboard/pages/success"
        )
    }

    @PostMapping("/onboard/2")
    fun index() = Screen(
        id = com.wutsi.application.login.endpoint.Page.ONBOARD,
        safe = true,
        appBar = null,
        child = PageView(
            children = PAGE_URLS.map {
                Page(url = urlBuilder.build(it))
            }
        )
    ).toWidget()
}
