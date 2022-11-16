package com.wutsi.application.membership.onboard.dao

import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.application.membership.onboard.exception.OnboardEntityNotFoundException
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class OnboardRepository(
    private val cache: Cache,
    private val tracingContext: TracingContext
) {
    fun save(account: OnboardEntity) {
        cache.put(getKey(), account)
    }

    fun delete() {
        cache.evict(getKey())
    }

    fun get(): OnboardEntity =
        cache.get(getKey(), OnboardEntity::class.java)
            ?: throw OnboardEntityNotFoundException()

    private fun getKey() = tracingContext.deviceId()
}
