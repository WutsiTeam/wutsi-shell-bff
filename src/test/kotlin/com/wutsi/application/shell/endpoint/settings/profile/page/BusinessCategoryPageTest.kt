package com.wutsi.application.shell.endpoint.settings.profile.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.ListCategoryResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BusinessCategoryPageTest : AbstractEndpointTest() {
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
            Category(id = 1, title = "category1"),
            Category(id = 2, title = "category2"),
            Category(id = 3, title = "category3")
        )
        doReturn(ListCategoryResponse(categories)).whenever(accountApi).listCategories()

        // THEN
        // THEN
        assertEndpointEquals("/pages/settings/profile/category.json", url)
    }
}
