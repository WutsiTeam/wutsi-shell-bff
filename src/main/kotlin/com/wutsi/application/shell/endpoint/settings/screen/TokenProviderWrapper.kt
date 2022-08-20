package com.wutsi.application.shell.endpoint.settings.screen

import com.wutsi.platform.core.security.TokenProvider
import org.springframework.stereotype.Service

/**
 * Wrapper of TokenProvider that can be mocked in integration testing
 */
@Service
class TokenProviderWrapper(
    private val tokenProvider: TokenProvider
) {
    fun getToken(): String? =
        tokenProvider.getToken()
}
