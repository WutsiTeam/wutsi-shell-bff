package com.wutsi.application.shell.endpoint.feedback.command

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.feedback.dto.SendFeedbackRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.mail.WutsiMailApi
import com.wutsi.platform.mail.dto.Message
import com.wutsi.platform.mail.dto.Party
import com.wutsi.platform.mail.dto.SendMessageRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/send-feedback")
class SendFeedbackCommand(
    private val mailApi: WutsiMailApi,
    private val securityContext: SecurityContext,
    private val tracingContext: TracingContext,
    private val tenantProvider: TenantProvider
) : AbstractCommand() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SendFeedbackRequest::class.java)
    }

    @PostMapping
    fun index(@RequestBody request: SendFeedbackRequest): Action {
        // Send
        try {
            send(request)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send the feedback", ex)
        }

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
                            url = "route:/~"
                        )
                    )
                )
            ).toWidget()
        )
    }

    private fun send(@RequestBody request: SendFeedbackRequest) {
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()
        mailApi.sendMessage(
            request = SendMessageRequest(
                recipient = Party(email = tenant.supportEmail),
                content = Message(
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
        )
    }
}
