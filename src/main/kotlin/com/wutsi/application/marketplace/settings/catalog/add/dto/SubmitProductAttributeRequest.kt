package com.wutsi.application.marketplace.settings.catalog.add.dto

import javax.validation.constraints.NotEmpty

data class SubmitProductAttributeRequest(
    @NotEmpty val value: String = ""
)
