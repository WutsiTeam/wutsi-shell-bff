package com.wutsi.application.shell.endpoint.fcm.dto

data class FCMRemoteMessage(
    val background: Boolean = false,
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val data: Map<String, String?>? = null
)
