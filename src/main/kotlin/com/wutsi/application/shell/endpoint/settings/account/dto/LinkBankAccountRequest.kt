package com.wutsi.application.shell.endpoint.settings.account.dto

data class LinkBankAccountRequest(
    val bankCode: String = "",
    val number: String = "",
    val ownerName: String = ""
)
