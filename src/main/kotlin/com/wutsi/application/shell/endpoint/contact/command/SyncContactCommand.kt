package com.wutsi.application.shell.endpoint.contact.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SyncContactRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/sync-contacts")
class SyncContactCommand(
    private val contactApi: WutsiContactApi
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: SyncContactRequest) {
        if (togglesProvider.isContactEnabled())
            contactApi.syncContacts(
                SyncContactRequest(
                    phoneNumbers = request.phoneNumbers
                )
            )
    }
}
