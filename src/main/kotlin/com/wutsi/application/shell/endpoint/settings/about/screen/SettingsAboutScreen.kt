package com.wutsi.application.shell.endpoint.settings.about.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/settings/about")
class SettingsAboutScreen(
    private val tenantProvider: TenantProvider,
    private val tracingContext: TracingContext,
    private val request: HttpServletRequest
) : AbstractQuery() {
    @PostMapping
    fun index(
        @RequestHeader(name = "X-Environment", required = false) env: String? = null
    ): Widget {
        val items = mutableListOf<WidgetAware>()
        val tenant = tenantProvider.get()
        val userId = securityContext.currentAccountId()

        tenant.logos.find { it.type == "PICTORIAL" }
            ?.let {
                items.add(
                    Container(
                        padding = 20.0,
                        alignment = Alignment.Center,
                        child = Image(
                            url = it.url,
                            width = 64.0,
                            height = 64.0
                        )
                    )
                )
            }

        val os = request.getHeader("X-OS") ?: ""
        val osVersion = request.getHeader("X-OS-Version") ?: ""
        val osInfo = "$os $osVersion"
        items.addAll(
            listOf(
                listItem("page.settings.about.app-name", tenant.name),
                listItem("page.settings.about.app-version", request.getHeader("X-Client-Version")),
                listItem("page.settings.about.app-os", osInfo),
                listItem("page.settings.about.device-id", tracingContext.deviceId()),
                listItem("page.settings.about.user-id", securityContext.currentAccountId().toString()),
                listItem("page.settings.about.environment", env?.uppercase() ?: "TEST")
            )
        )

        if (togglesProvider.isSwitchEnvironmentEnabled() && tenant.testUserIds.contains(userId)) {
            val nextEnv = if (env == "test") "prod" else "test"
            items.add(
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText(
                            "page.settings.about.button.switch-environment",
                            arrayOf(nextEnv.uppercase())
                        ),
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("/commands/switch-environment?environment=$nextEnv"),
                            prompt = Dialog(
                                type = DialogType.Confirm,
                                title = getText("prompt.confirm.title"),
                                message = getText(
                                    "page.settings.about.switch-environment-confirm",
                                    arrayOf(nextEnv.uppercase())
                                )
                            ).toWidget()
                        )
                    )
                )
            )
        }

        return Screen(
            id = Page.SETTINGS_ABOUT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.about.app-bar.title")
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = items
                )
            )
        ).toWidget()
    }

    private fun listItem(key: String, value: String?) = Row(
        children = listOf(
            Flexible(
                flex = 1,
                child = Container(
                    padding = 5.0,
                    child = Text(
                        getText(key),
                        bold = true,
                        alignment = TextAlignment.Right,
                        size = Theme.TEXT_SIZE_SMALL
                    )
                )
            ),
            Flexible(
                flex = 3,
                child = Container(
                    padding = 5.0,
                    child = Text(value ?: "", alignment = TextAlignment.Left, size = Theme.TEXT_SIZE_SMALL)
                )
            )
        )
    )
}
