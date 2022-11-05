package com.wutsi.application.login.exception

import com.wutsi.platform.core.error.Error

class AuthenticationException(message: String, val error: Error? = null, cause: Throwable? = null) : RuntimeException(message, cause)
