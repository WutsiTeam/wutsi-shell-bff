package com.wutsi.application.login.endpoint.onboard.page

import com.wutsi.application.login.endpoint.AbstractQuery

abstract class AbstractOnboardQuery : AbstractQuery() {
    protected fun getPhoneNumber(): String {
        val state = getState()
        return formattedPhoneNumber(state.phoneNumber, state.country)!!
    }
}
