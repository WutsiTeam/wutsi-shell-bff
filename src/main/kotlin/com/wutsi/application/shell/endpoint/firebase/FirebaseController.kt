package com.wutsi.application.shell.endpoint.firebase

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.firebase.dto.HandleMessageRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/firebase")
class FirebaseController : AbstractQuery() {
    @PostMapping("/on-message")
    fun onMessage(
        @RequestBody request: HandleMessageRequest
    ) {
        log(request)
    }

    @PostMapping("/on-select")
    fun onSelect(
        @RequestBody request: HandleMessageRequest
    ) {
        log(request)
    }

    private fun log(request: HandleMessageRequest) {
        logger.add("message_title", request.title)
        logger.add("message_body", request.body)
        logger.add("message_image_url", request.imageUrl)
        logger.add("message_background", request.background)
        logger.add("message_data", request.data)
    }
}
