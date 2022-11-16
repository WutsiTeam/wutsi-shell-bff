package com.wutsi.application

object Page {
    const val HOME = "page.home"
    const val ONBOARD = "page.onboard"
    const val LOGIN = "page.login"
    const val SETTINGS = "page.settings"

    fun getHomeUrl() = "/2"
    fun getOnboardUrl() = "/onboard/2"
    fun getLoginUrl() = "/login/2"
    fun getSettingsUrl() = "/settings/2"
    fun getProfileUrl() = "/profile/2"
    fun getOrdersUrl() = "/orders/2"
}
