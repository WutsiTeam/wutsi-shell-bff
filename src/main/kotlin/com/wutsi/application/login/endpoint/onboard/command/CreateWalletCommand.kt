package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.application.login.entity.AccountEntity
import com.wutsi.application.login.exception.PhoneAlreadyAssignedException
import com.wutsi.application.service.OnboardService
import com.wutsi.flutter.sdui.Action
import com.wutsi.platform.security.dto.AuthenticationRequest
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/create-wallet")
class CreateWalletCommand(
    @Value("\${wutsi.platform.security.api-key}") private var apiKey: String
) : AbstractOnboardCommand() {
    @PostMapping
    fun submit(): ResponseEntity<Action> {
        val accessToken = createWallet()

        val headers = HttpHeaders()
        headers["x-access-token"] = accessToken
        headers["x-onboarded"] = "true"
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                gotoRoute("/", true)
            )
    }

    private fun createWallet(): String {
        val state = getState()
        try {
            val accountId = createAccount(state)
            logger.add("account_id", accountId)
            return authenticate(state)
        } catch (ex: FeignException) {
            val response = toErrorResponse(ex)
            if (response.error.code == OnboardService.ACCOUNT_ALREADY_ASSIGNED) {
                throw PhoneAlreadyAssignedException(state.phoneNumber)
            }
            throw ex
        } finally {
            log(state)
        }
    }

    protected fun authenticate(state: AccountEntity): String =
        securityApi.authenticate(
            AuthenticationRequest(
                type = "runas",
                phoneNumber = state.phoneNumber,
                apiKey = apiKey
            )
        ).accessToken
}
