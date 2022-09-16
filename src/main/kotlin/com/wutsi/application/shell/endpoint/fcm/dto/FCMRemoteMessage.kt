package com.wutsi.application.shell.endpoint.fcm.dto

data class FCMRemoteMessage(
    val title: String? = null,
    val body: String? = null,
    val data: Map<String, String?>? = null
)
