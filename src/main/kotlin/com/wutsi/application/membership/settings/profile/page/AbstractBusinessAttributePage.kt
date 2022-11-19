package com.wutsi.application.membership.settings.profile.page

import com.wutsi.application.membership.settings.profile.service.ProfileEditorWidgetProvider
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBusinessAttributePage : AbstractBusinessPage() {
    @Autowired
    protected lateinit var widgetProvider: ProfileEditorWidgetProvider

    protected abstract fun getAttribute(): String

    override fun getTitle() = getText("page.settings.profile.attribute.${getAttribute()}")

    override fun getSubTitle() = getText("page.settings.profile.attribute.${getAttribute()}.description")

    override fun getBody(): WidgetAware? {
        val member = membershipManagerApi.getMember().member
        return Container(
            padding = 10.0,
            child = widgetProvider.get(getAttribute(), member)
        )
    }
}
