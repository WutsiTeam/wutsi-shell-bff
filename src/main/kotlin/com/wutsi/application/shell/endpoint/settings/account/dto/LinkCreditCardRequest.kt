package com.wutsi.application.shell.endpoint.settings.account.dto

data class LinkCreditCardRequest(
    val number: String = "",
    val expiryMonth: Int = -1,
    val expiryYear: Int = -1,
    val ownerName: String = ""
)
