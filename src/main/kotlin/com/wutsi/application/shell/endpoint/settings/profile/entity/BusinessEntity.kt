package com.wutsi.application.shell.endpoint.settings.profile.entity

data class BusinessEntity(
    var displayName: String = "",
    var biography: String? = null,
    var categoryId: Long? = null,
    var cityId: Long? = null,
    var whatsapp: String? = null,
    var street: String? = null
) : java.io.Serializable
