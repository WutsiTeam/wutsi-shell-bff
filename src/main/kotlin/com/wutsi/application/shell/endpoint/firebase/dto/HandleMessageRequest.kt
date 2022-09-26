package com.wutsi.application.shell.endpoint.firebase.dto

data class HandleMessageRequest(
    val background: Boolean = false,
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val data: Map<String, String?>? = null
)
