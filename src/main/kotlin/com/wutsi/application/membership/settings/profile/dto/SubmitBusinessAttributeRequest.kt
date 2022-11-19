package com.wutsi.application.membership.settings.profile.dto

import javax.validation.constraints.NotEmpty

data class SubmitBusinessAttributeRequest(
    @NotEmpty val value: String = ""
)
