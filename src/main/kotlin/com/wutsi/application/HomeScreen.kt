package com.wutsi.application

import com.wutsi.application.store.endpoint.marketplace.MarketplaceScreen
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.platform.account.WutsiAccountApi
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    catalogApi: WutsiCatalogApi,
    accountApi: WutsiAccountApi
) : MarketplaceScreen(catalogApi, accountApi)
