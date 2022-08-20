package com.wutsi.application.shell.endpoint.track.dto

data class EventRequest(
    val event: String = "",
    val productId: String? = null
)
