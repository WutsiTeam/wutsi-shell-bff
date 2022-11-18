package com.wutsi.application.membership.settings.profile.dao

import com.wutsi.application.membership.settings.profile.entity.BusinessEntity
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class BusinessRepository(
    private val cache: Cache,
    private val tracingContext: TracingContext
) {
    fun save(email: BusinessEntity) {
        cache.put(getKey(), email)
    }

    fun delete() {
        cache.evict(getKey())
    }

    fun get(): BusinessEntity =
        cache.get(getKey(), BusinessEntity::class.java)
            ?: BusinessEntity()

    private fun getKey() = tracingContext.deviceId()
}
