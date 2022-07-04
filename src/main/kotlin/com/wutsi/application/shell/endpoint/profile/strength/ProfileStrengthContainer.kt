package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class ProfileStrengthContainer(
    private val picture: ProfileStrengthPicture,
    private val whatsapp: ProfileStrengthWhatsapp,
    private val paymentMethod: ProfileStrengthPaymentMethod,
    private val email: ProfileStrengthEmailWidget,
    private val location: ProfileStrengthLocationWidget,
    private val shipping: ProfileStrengthShipping,
    private val cache: Cache,
    private val tracingContext: TracingContext,
    private val logger: KVLogger,
) : ProfileStrengthWidget {
    companion object {
        const val DISPLAY_DELAY_MILLIS = 60 * 60 * 1000 // 1 hour
    }

    override fun toWidget(account: Account): WidgetAware? {
        if (!canDisplay())
            return null

        val all = createComponents()
        val widgets = all.mapNotNull { it.toWidget(account) }
        return if (widgets.isEmpty())
            null
        else
            try {
                Column(
                    children = listOf(
                        widgets.random().let {
                            Container(
                                padding = 10.0,
                                margin = 10.0,
                                background = Theme.COLOR_PRIMARY_LIGHT,
                                borderColor = Theme.COLOR_PRIMARY,
                                border = 1.0,
                                borderRadius = 5.0,
                                child = it
                            )
                        }
                    )
                )
            } finally {
                displayed()
            }
    }

    private fun createComponents(): List<ProfileStrengthWidget> =
        listOf(
            paymentMethod,
            whatsapp,
            location,
            shipping,
            email,
            picture,
        )

    private fun canDisplay(): Boolean {
        try {
            val value = cache.get(cacheKey(), Long::class.java)
                ?: return true

            logger.add("profile_strength_last_displayed", value)
            return System.currentTimeMillis() - value > DISPLAY_DELAY_MILLIS
        } catch (ex: Exception) {
            return false
        }
    }

    private fun displayed() {
        cache.put(cacheKey(), System.currentTimeMillis())
    }

    private fun cacheKey(): String =
        "${tracingContext.deviceId()}-profile-strength"
}
