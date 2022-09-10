package com.wutsi.application.shell.endpoint.feedback.command

import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.feedback.dto.SendFeedbackRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/send-feedback")
class SendFeedbackCommand(
    private val provider: MessagingServiceProvider,
    private val tracingContext: TracingContext,
    private val tenantProvider: TenantProvider
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: SendFeedbackRequest): Action {
        // Send
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()
        val id = provider.get(MessagingType.EMAIL).send(
            message = Message(
                recipient = Party(email = tenant.supportEmail),
                mimeType = "text/plain",
                subject = "User Feedback",
                body = """
                        ${request.message}

                        --------------------------------------

                        User: ${user.id} - ${user.displayName}
                        Device-ID: ${tracingContext.deviceId()}
                        Trace-ID: ${tracingContext.traceId()}
                        Client-Info: ${tracingContext.clientInfo()}
                """.trimIndent()
            )
        )
        logger.add("sender_id", user.id)
        logger.add("sender_fullname", user.displayName)
        logger.add("message_id", id)

        // Result
        return Action(
            type = ActionType.Prompt,
            prompt = Dialog(
                type = DialogType.Information,
                message = getText("page.feedback.sent"),
                actions = listOf(
                    Button(
                        caption = getText("page.feedback.button.ok"),
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/.."
                        )
                    )
                )
            ).toWidget()
        )
    }
}
