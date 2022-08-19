package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.profile.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.EnableBusinessRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EnableBusinessCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun enable() {
        // GIVEN
        val data = BusinessEntity(
            displayName = "Yo",
            biography = "this is the bio",
            categoryId = 555L,
            cityId = 444L,
            whatsapp = "+1514555954954",
            street = "4403940394"
        )
        doReturn(data).whenever(cache).get(any(), any<Class<*>>())

        // WHEN
        val url = "http://localhost:$port/commands/enable-business"
        val response = rest.postForEntity(url, null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(accountApi).enableBusiness(
            ACCOUNT_ID,
            EnableBusinessRequest(
                displayName = data.displayName,
                country = "CM",
                cityId = data.cityId,
                street = data.street,
                whatsapp = data.whatsapp,
                categoryId = data.categoryId,
                biography = data.biography
            )
        )

        verify(cache).evict(any())
    }
}
