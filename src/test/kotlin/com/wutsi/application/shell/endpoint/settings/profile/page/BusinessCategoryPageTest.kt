package com.wutsi.application.shell.endpoint.settings.profile.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractShellEndpointTest
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.ListCategoryResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BusinessCategoryPageTest : AbstractShellEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/business/pages/category"
    }

    @Test
    fun invoke() {
        // GIVEN
        val categories = listOf(
            Category(id = 1, title = "Art"),
            Category(id = 2, title = "Beauté"),
            Category(id = 3, title = "Club de Sport"),
            Category(id = 4, title = "Écrivain"),
            Category(id = 5, title = "Garage")
        )
        doReturn(ListCategoryResponse(categories)).whenever(accountApi).listCategories()

        // THEN
        assertEndpointEquals("/shell/pages/settings/profile/category.json", url)
    }
}
