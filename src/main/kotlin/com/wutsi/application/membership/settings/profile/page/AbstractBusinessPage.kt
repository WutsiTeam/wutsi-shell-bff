package com.wutsi.application.membership.settings.profile.page

import com.wutsi.application.AbstractEndpoint
import com.wutsi.application.membership.settings.profile.dao.BusinessRepository
import com.wutsi.application.membership.settings.profile.entity.BusinessEntity
import com.wutsi.application.membership.settings.profile.service.ProfileEditorWidgetProvider
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBusinessPage : AbstractEndpoint() {
    @Autowired
    private lateinit var widgetProvider: ProfileEditorWidgetProvider

    @Autowired
    private lateinit var dao: BusinessRepository

    @Autowired
    private lateinit var membershipManagerApi: MembershipManagerApi

    protected fun getEntity(): BusinessEntity =
        dao.get() ?: BusinessEntity()
}
