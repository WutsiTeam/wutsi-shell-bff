package com.wutsi.application.membership.settings.security.dao

import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class PasscodeRepository(
    private val cache: Cache,
    private val tracingContext: TracingContext
) {
    fun save(account: PasscodeEntity) {
        cache.put(getKey(), account)
    }

    fun delete() {
        cache.evict(getKey())
    }

    fun get(): PasscodeEntity =
        cache.get(getKey(), PasscodeEntity::class.java)
            ?: PasscodeEntity()

    private fun getKey() = tracingContext.deviceId()
}
