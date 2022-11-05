package com.wutsi.application.login.endpoint

import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType

abstract class AbstractCommand : AbstractEndpoint() {
    protected fun promptError(errorKey: String) = Action(
        type = ActionType.Prompt,
        prompt = Dialog(
            title = getText("prompt.error.title"),
            type = DialogType.Error,
            message = getText(errorKey)
        ).toWidget()
    )

    protected fun promptInformation(errorKey: String) = Action(
        type = ActionType.Prompt,
        prompt = Dialog(
            title = getText("prompt.information.title"),
            type = DialogType.Information,
            message = getText(errorKey)
        ).toWidget()
    )
}
