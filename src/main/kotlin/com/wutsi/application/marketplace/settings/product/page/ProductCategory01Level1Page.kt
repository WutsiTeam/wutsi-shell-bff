package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dao.CategoryRepository
import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/category/pages/level-1")
class ProductCategory01Level1Page(
    private val dao: CategoryRepository,
) : AbstractProductCategoryPage() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getPageIndex() = PAGE_INDEX
    override fun getParentCategoryId(): Long? = dao.get().category0Id
    override fun getSubmitUrl() = "${Page.getSettingsProductCategoryUrl()}/pages/level-1/submit"

    @PostMapping("/submit")
    fun submit(
        @RequestParam("category-id") categoryId: Long,
    ): Action {
        val entity = dao.get()
        entity.category1Id = categoryId
        dao.save(entity)
        return gotoNextPage()
    }
}
