package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.membership.settings.security.dao.PasscodeRepository
import com.wutsi.application.membership.settings.security.dto.SubmitPasscodeRequest
import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode/pages/pin")
class Passcode00PINPage(
    private val dao: PasscodeRepository
) : AbstractEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    @PostMapping
    fun index(): Widget =
        Column(
            children = listOf(
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        IconButton(
                            icon = Theme.ICON_ARROW_BACK,
                            color = Theme.COLOR_BLACK,
                            action = gotoPreviousScreen()
                        )
                    )
                ),
                Container(
                    alignment = Alignment.Center,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.settings.passcode.pin.sub-title"),
                        alignment = TextAlignment.Center,
                        size = Theme.TEXT_SIZE_LARGE
                    )
                ),
                PinWithKeyboard(
                    name = "pin",
                    hideText = true,
                    pinSize = 20.0,
                    maxLength = 6,
                    action = Action(
                        type = Command,
                        url = urlBuilder.build("/security/passcode/pages/pin/submit")
                    )
                )
            )
        ).toWidget()

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPasscodeRequest): Action {
        dao.save(
            PasscodeEntity(
                pin = request.pin
            )
        )
        return gotoPage(
            PAGE_INDEX + 1
        )
    }
}
