package com.wutsi.application.membership.settings.profile.entity

data class BusinessEntity(
    var displayName: String = "",
    var biography: String? = null,
    var categoryId: Long = -1,
    var cityId: Long = -1,
    var whatsapp: Boolean = false
) : java.io.Serializable
