package com.wutsi.application.membership.settings.profile.dao

import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class EmailRepository(
    private val cache: Cache,
    private val tracingContext: TracingContext
) {
    fun save(email: EmailEntity) {
        cache.put(getKey(), email)
    }

    fun delete() {
        cache.evict(getKey())
    }

    fun get(): EmailEntity =
        cache.get(getKey(), EmailEntity::class.java)
            ?: EmailEntity()

    private fun getKey() = tracingContext.deviceId()
}
