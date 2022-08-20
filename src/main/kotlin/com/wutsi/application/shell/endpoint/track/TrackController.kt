package com.wutsi.application.shell.endpoint.track

import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.dto.Track
import com.wutsi.analytics.tracking.entity.EventType
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.track.dto.EventRequest
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/track")
class TrackController(
    private val tenantProvider: TenantProvider,
    private val tracingContext: TracingContext,
    private val httpRequest: HttpServletRequest,
    private val trackingApi: WutsiTrackingApi
) : AbstractQuery() {
    @PostMapping("/load")
    fun load(
        @RequestParam(name = "screen-id") screenId: String
    ) {
        onEvent(screenId, EventType.LOAD.name)
    }

    @PostMapping("/action")
    fun action(
        @RequestParam(name = "screen-id") screenId: String,
        @RequestBody request: EventRequest
    ) {
        onEvent(screenId, request.event, request.productId)
    }

    private fun onEvent(screenId: String, event: String, productId: String? = null) {
        trackingApi.push(
            request = PushTrackRequest(
                track = Track(
                    time = System.currentTimeMillis(),
                    tenantId = tenantProvider.tenantId().toString(),
                    deviceId = tracingContext.deviceId(),
                    accountId = securityContext.currentAccountId().toString(),
                    productId = productId,
                    page = screenId,
                    event = event,
                    ua = httpRequest.getHeader("User-Agent"),
                    referer = httpRequest.getHeader("Referer"),
                    ip = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
                )
            )
        )
    }
}
