package com.wutsi.application

object Page {
    const val ABOUT = "page.about"
    const val HOME = "page.home"
    const val ONBOARD = "page.onboard"
    const val LOGIN = "page.login"
    const val ORDER = "page.order"
    const val ORDER_LIST = "page.order.list"
    const val PRODUCT = "page.product"
    const val PROFILE = "page.profile"
    const val SECURITY = "page.security"
    const val SECURITY_DELETE = "page.security.delete"
    const val SECURITY_PASSCODE = "page.security.passcode"
    const val SETTINGS = "page.settings"
    const val SETTINGS_ACCOUNT = "page.account"
    const val SETTINGS_ACCOUNT_CASHOUT = "page.account.cashout"
    const val SETTINGS_ACCOUNT_ADD_MOBILE = "page.account.add.mobile"
    const val SETTINGS_ACCOUNT_LIST = "page.account.list"
    const val SETTINGS_BUSINESS = "page.settings.business"
    const val SETTINGS_DISCOUNT = "page.settings.discount"
    const val SETTINGS_DISCOUNT_ADD = "page.settings.discount.add"
    const val SETTINGS_DISCOUNT_LIST = "page.settings.discount.list"
    const val SETTINGS_DISCOUNT_PRODUCTS = "page.settings.discount.products"
    const val SETTINGS_PICTURE = "page.settings.picture"
    const val SETTINGS_PROFILE = "page.settings.profile"
    const val SETTINGS_PROFILE_EDITOR = "page.settings.profile.editor"
    const val SETTINGS_PROFILE_EMAIL_VERIFICATION = "page.settings.profile.email.verification"
    const val SETTINGS_STORE = "page.settings.store"
    const val SETTINGS_STORE_ENABLE = "page.settings.store.enable"
    const val SETTINGS_STORE_STATS = "page.settings.store.stats"
    const val SETTINGS_CATALOG = "page.settings.catalog"
    const val SETTINGS_CATALOG_ADD = "page.settings.catalog.add"
    const val SETTINGS_CATALOG_PRODUCT = "page.settings.catalog.product"
    const val SETTINGS_CATALOG_EDITOR = "page.settings.catalog.editor"
    const val SETTINGS_CATALOG_EDITOR_EVENT = "page.settings.catalog.editor.event"
    const val SETTINGS_CATALOG_EDITOR_FILE = "page.settings.catalog.editor.file"
    const val SETTINGS_CATALOG_PICTURE = "page.settings.catalog.picture"
    const val TRANSACTION = "page.transaction"
    const val TRANSACTION_LIST = "page.transaction.list"

    fun getAboutUrl() = "/about"
    fun getHomeUrl() = "/2"
    fun getLoginUrl() = "/login/2"
    fun getOnboardUrl() = "/onboard/2"
    fun getOrderUrl() = "/orders/2"
    fun getOrderListUrl() = "/orders/2/list"
    fun getSecurityUrl() = "/security"
    fun getSettingsUrl() = "/settings/2"
    fun getSettingsAccountUrl() = "${getSettingsUrl()}/accounts"
    fun getSettingsAccountListUrl() = "${getSettingsAccountUrl()}/list"
    fun getSettingsAccountCashoutUrl() = "${getSettingsAccountUrl()}/cashout"
    fun getSettingsBusinessUrl() = "${getSettingsUrl()}/business"
    fun getSettingsProfileUrl() = "${getSettingsUrl()}/profile"
    fun getSettingsProfileEditorUrl() = "${getSettingsProfileUrl()}/editor"
    fun getSettingsStoreUrl() = "${getSettingsUrl()}/store"
    fun getSettingsStoreActivateUrl() = "${getSettingsUrl()}/store/activate"
    fun getSettingsStoreStats() = "${getSettingsStoreUrl()}/stats"
    fun getSettingsProductUrl() = "${getSettingsUrl()}/products"
    fun getSettingsProductPictureUrl() = "${getSettingsProductUrl()}/pictures"
    fun getSettingsProductEditorUrl() = "${getSettingsProductUrl()}/editor"
    fun getSettingsProductAddUrl() = "${getSettingsProductUrl()}/add"
    fun getSettingsProductListUrl() = "${getSettingsProductUrl()}/list"
    fun getSettingsDiscountUrl() = "${getSettingsUrl()}/discounts"
    fun getSettingsDiscountAddUrl() = "${getSettingsDiscountUrl()}/add"
    fun getSettingsDiscountListUrl() = "${getSettingsDiscountUrl()}/list"
    fun getSettingsDiscountEditorUrl() = "${getSettingsDiscountUrl()}/editor"
    fun getSettingsDiscountProductUrl() = "${getSettingsDiscountUrl()}/products"
    fun getProfileUrl() = "/profile/2"
    fun getProductUrl() = "/products/2"
    fun getProductListUrl() = "/products/2/list"
    fun getTransactionUrl() = "/transactions/2"
    fun getTransactionListUrl() = "${getTransactionUrl()}/list"
}
