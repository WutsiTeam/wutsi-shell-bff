package com.wutsi.application.servlet

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.UnauthorizedException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.security.TokenBlacklistService
import com.wutsi.platform.core.security.TokenProvider
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class TokenVerifierFilter(
    private val blacklist: TokenBlacklistService,
    private val tokenProvider: TokenProvider,
    private val requestMatcher: RequestMatcher,
    private val logger: KVLogger,
) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (shouldVerifyToken(request as HttpServletRequest)) {
            verifyBlacklist()
        }
        chain.doFilter(request, response)
    }

    private fun verifyBlacklist() {
        val token = tokenProvider.getToken()
        if (token != null && blacklist.contains(token)) {
            logger.add("token_blacklisted", true)
            throw UnauthorizedException(
                error = Error(),
            )
        } else {
            logger.add("token_blacklisted", false)
        }
    }

    private fun shouldVerifyToken(request: HttpServletRequest): Boolean =
        requestMatcher.matches(request)
}
