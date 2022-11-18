package com.wutsi.application

object Page {
    const val ABOUT = "page.about"
    const val HOME = "page.home"
    const val ONBOARD = "page.onboard"
    const val LOGIN = "page.login"
    const val SECURITY = "page.security"
    const val SECURITY_DELETE = "page.security.delete"
    const val SECURITY_PASSCODE = "page.security.passcode"
    const val SETTINGS = "page.settings"

    fun getAboutUrl() = "/about"
    fun getHomeUrl() = "/2"
    fun getLoginUrl() = "/login/2"
    fun getOnboardUrl() = "/onboard/2"
    fun getOrdersUrl() = "/orders/2"
    fun getProfileUrl() = "/profile/2"
    fun getSecurityUrl() = "/security"
    fun getSettingsUrl() = "/settings/2"
    fun getStoreUrl() = "/store/2"
}
