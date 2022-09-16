package com.wutsi.application.shell.endpoint.fcm.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.fcm.dto.FCMRemoteMessage
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/fcm-message-received")
class MessageReceivedCommand : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: FCMRemoteMessage) {
        logger.add("message_title", request.title)
        logger.add("message_body", request.body)
        logger.add("message_image_url", request.imageUrl)
        logger.add("message_background", request.background)
        logger.add("message_data", request.data)
    }
}
