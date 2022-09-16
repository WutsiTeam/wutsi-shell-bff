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
        logger.add("title", request.title)
        logger.add("body", request.body)
        logger.add("background", request.background)
        logger.add("data", request.data)
    }
}
