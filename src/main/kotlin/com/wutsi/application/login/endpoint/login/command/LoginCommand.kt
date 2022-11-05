package com.wutsi.application.login.endpoint.login.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.login.endpoint.AbstractCommand
import com.wutsi.application.login.endpoint.login.dto.LoginRequest
import com.wutsi.application.login.exception.AuthenticationException
import com.wutsi.application.shared.service.PhoneUtil
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/login")
class LoginCommand(
    private val accountApi: WutsiAccountApi,
    private val securityApi: WutsiSecurityApi,
    private val urlBuilder: URLBuilder,
    private val mapper: ObjectMapper,

    @Value("\${wutsi.platform.security.api-key}") private val apiKey: String
) : AbstractCommand() {
    @PostMapping
    fun submit(
        @RequestParam(name = "phone") phoneNumber: String,
        @RequestParam(name = "auth", required = false, defaultValue = "true") auth: Boolean = true,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "return-to-route", required = false, defaultValue = "true") returnToRoute: Boolean = true,
        @Valid @RequestBody
        request: LoginRequest
    ): ResponseEntity<Action> {
        val accessToken = login(PhoneUtil.sanitize(phoneNumber), auth, request)

        val headers = HttpHeaders()
        if (accessToken != null) {
            headers["x-access-token"] = accessToken
        }

        val action = returnUrl
            ?.let { gotoUrl(it, actionType(returnToRoute), if (returnToRoute) true else null) }
            ?: gotoUrl(urlBuilder.build(shellUrl, ""), ActionType.Route, replacement = true)
        logger.add("action_url", action.url)
        logger.add("action_type", action.type)
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(action)
    }

    private fun actionType(returnToRoute: Boolean): ActionType =
        if (returnToRoute) ActionType.Route else ActionType.Command

    fun login(phoneNumber: String, auth: Boolean, request: LoginRequest): String? {
        logger.add("phone_number", phoneNumber)
        logger.add("auth", auth)
        try {
            // Check password
            val account = findAccount(phoneNumber)
            accountApi.checkPassword(account.id, request.pin)

            // Authenticate
            return if (auth) {
                authenticate(phoneNumber)
            } else {
                null
            }
        } catch (ex: FeignException) {
            val response = toErrorResponse(ex)
            throw AuthenticationException("Authentication failed", response?.error)
        }
    }

    private fun findAccount(phoneNumber: String): AccountSummary {
        val accounts = accountApi.searchAccount(
            SearchAccountRequest(
                phoneNumber = phoneNumber,
                status = AccountStatus.ACTIVE.name
            )
        ).accounts
        if (accounts.isNotEmpty()) {
            val account = accounts[0]
            if (account.status != "ACTIVE") {
                throw AuthenticationException("Account not active")
            }
            return account
        } else {
            throw AuthenticationException("Account not found")
        }
    }

    private fun authenticate(phoneNumber: String): String {
        val accessToken = securityApi.authenticate(
            AuthenticationRequest(
                type = "runas",
                phoneNumber = phoneNumber,
                apiKey = apiKey
            )
        ).accessToken
        logger.add("access_token", "***")

        return accessToken
    }

    private fun toErrorResponse(ex: FeignException): ErrorResponse? =
        try {
            mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }
}
