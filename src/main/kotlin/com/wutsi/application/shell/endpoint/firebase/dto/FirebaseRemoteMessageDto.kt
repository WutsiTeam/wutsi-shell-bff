package com.wutsi.application.shell.endpoint.firebase.dto

data class FirebaseRemoteMessageDto(
    val background: Boolean = false,
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val data: Map<String, String?>? = null,
    val deviceId: String? = null
)
