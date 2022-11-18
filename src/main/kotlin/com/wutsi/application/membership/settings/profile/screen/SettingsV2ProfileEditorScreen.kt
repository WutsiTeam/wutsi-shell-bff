package com.wutsi.application.membership.settings.profile.screen

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dao.EmailRepository
import com.wutsi.application.membership.settings.profile.dto.SubmitProfileAttributeRequest
import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import com.wutsi.application.membership.settings.profile.service.ProfileEditorWidget
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/profile/editor")
class SettingsV2ProfileEditorScreen(
    private val editor: ProfileEditorWidget,
    private val dao: EmailRepository,
    private val membershipManagerApi: MembershipManagerApi,
    private val securityManagerApi: SecurityManagerApi
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam name: String): Widget {
        val member = membershipManagerApi.getMember().member
        return Screen(
            id = Page.SETTINGS_PROFILE_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.attribute.$name")
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            getText("page.settings.profile.attribute.$name.description")
                        )
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Container(
                        padding = 10.0,
                        child = editor.getWidget(name, member)
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.attribute.button.submit"),
                            action = executeCommand(
                                urlBuilder.build("${Page.getSettingsUrl()}/profile/editor/submit?name=$name")
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(
        @RequestParam name: String,
        @RequestBody request: SubmitProfileAttributeRequest
    ): Action {
        if (name == "email") {
            val member = membershipManagerApi.getMember().member
            if (member.email.equals(request.value, true)) {
                return gotoPreviousScreen()
            }

            val token = securityManagerApi.createOtp(
                request = CreateOTPRequest(
                    address = request.value,
                    type = MessagingType.EMAIL.name
                )
            ).token
            dao.save(
                EmailEntity(
                    value = request.value,
                    token = token
                )
            )

            return gotoUrl(
                url = urlBuilder.build("${Page.getSettingsUrl()}/profile/email/verification"),
                replacement = true
            )
        } else {
            membershipManagerApi.updateMemberAttribute(
                request = UpdateMemberAttributeRequest(
                    name = name,
                    value = request.value
                )
            )

            return gotoPreviousScreen()
        }
    }
}
