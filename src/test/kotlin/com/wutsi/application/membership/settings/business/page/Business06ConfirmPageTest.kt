package com.wutsi.application.membership.settings.business.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.EnableBusinessRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Business06ConfirmPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
        categoryId = 22L,
        cityId = 100L,
        whatsapp = false,
        biography = "Yo man"
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/confirm$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/confirm.json", url())

    @Test
    fun submit() {
        // WHEN
        val response = rest.postForEntity(url("/submit"), null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/7", action.url)

        verify(membershipManagerApi).enableBusiness(
            request = EnableBusinessRequest(
                displayName = entity.displayName,
                biography = entity.biography,
                whatsapp = entity.whatsapp,
                cityId = entity.cityId!!,
                categoryId = entity.categoryId!!
            )
        )
    }
}
