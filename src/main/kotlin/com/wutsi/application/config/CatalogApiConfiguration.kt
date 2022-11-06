package com.wutsi.application.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.service.FeignAcceptLanguageInterceptor
import com.wutsi.ecommerce.catalog.Environment.PRODUCTION
import com.wutsi.ecommerce.catalog.Environment.SANDBOX
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.WutsiCatalogApiBuilder
import com.wutsi.platform.core.security.feign.FeignApiKeyRequestInterceptor
import com.wutsi.platform.core.security.feign.FeignAuthorizationRequestInterceptor
import com.wutsi.platform.core.tracing.feign.FeignTracingRequestInterceptor
import com.wutsi.platform.core.util.feign.Custom5XXErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import javax.servlet.http.HttpServletRequest

@Configuration
class CatalogApiConfiguration(
    private val authorizationRequestInterceptor: FeignAuthorizationRequestInterceptor,
    private val tracingRequestInterceptor: FeignTracingRequestInterceptor,
    private val acceptLanguageInterceptor: FeignAcceptLanguageInterceptor,
    private val apiKeyInterceptor: FeignApiKeyRequestInterceptor,
    private val mapper: ObjectMapper,
    private val env: Environment
) {
    @Bean
    fun catalogApi(request: HttpServletRequest): WutsiCatalogApi =
        WutsiCatalogApiBuilder().build(
            env = environment(),
            mapper = mapper,
            interceptors = listOf(
                tracingRequestInterceptor,
                authorizationRequestInterceptor,
                acceptLanguageInterceptor,
                apiKeyInterceptor
            ),
            errorDecoder = Custom5XXErrorDecoder()
        )

    private fun environment(): com.wutsi.ecommerce.catalog.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            PRODUCTION
        else
            SANDBOX
}