package com.wutsi.application.shell.endpoint.profile.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.CreateContactRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/add-contact")
class AddContactCommand(
    private val contactApi: WutsiContactApi
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam(name = "contact-id") contactId: Long): Action {
        contactApi.createContact(
            CreateContactRequest(
                contactId = contactId
            )
        )
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("profile?id=$contactId")
        )
    }
}
