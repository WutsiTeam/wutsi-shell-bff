package com.wutsi.application.shell.entity

import java.io.Serializable

data class SmsCodeEntity(
    val phoneNumber: String = "",
    val carrier: String = "",
    val token: String = ""
) : Serializable
