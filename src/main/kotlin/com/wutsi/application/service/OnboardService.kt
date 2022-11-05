package com.wutsi.application.service

import com.wutsi.platform.core.util.URN

class OnboardService {
    companion object {
        val ACCOUNT_ALREADY_ASSIGNED: String =
            com.wutsi.platform.account.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn
        val DEVICE_NOT_FOUND: String = URN.of("error", "app-onboard", "device-not-found").value
    }
}
