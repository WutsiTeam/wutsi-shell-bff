package com.wutsi.application.shell.endpoint.settings.about.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.logout.command.LogoutCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/switch-environment")
class SwitchEnvironmntCommand(
    private val logoutCommand: LogoutCommand
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestParam environment: String
    ): ResponseEntity<Action> {
        // Logout
        logoutCommand.execute()

        // Set the new header
        val headers = HttpHeaders()
        headers.add("x-environment", environment)
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                Action(
                    type = ActionType.Route,
                    url = "route:/"
                )
            )
    }
}
