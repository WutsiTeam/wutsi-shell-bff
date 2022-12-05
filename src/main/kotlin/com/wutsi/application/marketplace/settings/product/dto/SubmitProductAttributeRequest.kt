package com.wutsi.application.marketplace.settings.product.dto

import javax.validation.constraints.NotEmpty

data class SubmitProductAttributeRequest(
    @NotEmpty val value: String = ""
)
