package com.wutsi.application.shell.endpoint.firebase

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.firebase.dto.FirebaseRemoteMessageDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/firebase")
class FirebaseController : AbstractQuery() {
    @PostMapping("/on-message")
    fun onMessage(
        @RequestBody request: FirebaseRemoteMessageDto
    ) {
        logger.add("message_title", request.title)
        logger.add("message_body", request.body)
        logger.add("message_image_url", request.imageUrl)
        logger.add("message_background", request.background)
        logger.add("message_data", request.data)
        logger.add("device_id", request.deviceId)
    }
}
