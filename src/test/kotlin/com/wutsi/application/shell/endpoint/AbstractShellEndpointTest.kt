package com.wutsi.application.shell.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.LanguageClientHttpRequestInterceptor
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.GetCategoryResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.CreditCardType
import com.wutsi.platform.tenant.dto.FinancialInstitution
import com.wutsi.platform.tenant.dto.GetTenantResponse
import com.wutsi.platform.tenant.dto.Logo
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.PhonePrefix
import com.wutsi.platform.tenant.dto.Tenant
import com.wutsi.platform.tenant.entity.FinancialInstitutionType
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.Cache
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractShellEndpointTest {
    companion object {
        const val USER_ID = 1L
        const val DEVICE_ID = "0000-1111"
        const val PHONE_NUMBER = "+2379505677"
        const val ACCOUNT_ID = 77777L
        const val ACCOUNT_NAME = "Ray Sponsible"
        const val TENANT_ID = "1"
    }

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var tracingContext: TracingContext

    @MockBean
    private lateinit var tenantApi: WutsiTenantApi

    @MockBean
    protected lateinit var accountApi: WutsiAccountApi

    @MockBean
    protected lateinit var cache: Cache

    @MockBean
    protected lateinit var securityApi: WutsiSecurityApi

    @Autowired
    private lateinit var messages: MessageSource

    protected lateinit var rest: RestTemplate

    lateinit var traceId: String

    lateinit var user: Account

    @BeforeEach
    fun setUp() {
        traceId = UUID.randomUUID().toString()
        doReturn(DEVICE_ID).whenever(tracingContext).deviceId()
        doReturn(traceId).whenever(tracingContext).traceId()
        doReturn(TENANT_ID).whenever(tracingContext).tenantId()

        val tenant = Tenant(
            id = 1,
            name = "test",
            logos = listOf(
                Logo(type = "PICTORIAL", url = "http://www.goole.com/images/1.png")
            ),
            countries = listOf("CM"),
            languages = listOf("en", "fr"),
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 CFA",
            currency = "XAF",
            currencySymbol = "CFA",
            dateFormat = "dd MMM yyyy",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyyy, HH:mm",
            domainName = "www.wutsi.com",
            mobileCarriers = listOf(
                MobileCarrier(
                    code = "mtn",
                    name = "MTN",
                    countries = listOf("CM", "CD"),
                    phonePrefixes = listOf(
                        PhonePrefix(
                            country = "CM",
                            prefixes = listOf("+23795")
                        )
                    ),
                    logos = listOf(
                        Logo(type = "PICTORIAL", url = "http://www.goole.com/images/mtn.png")
                    )
                ),
                MobileCarrier(
                    code = "orange",
                    name = "ORANGE",
                    countries = listOf("CM"),
                    phonePrefixes = listOf(
                        PhonePrefix(
                            country = "CM",
                            prefixes = listOf("+23722")
                        )
                    ),
                    logos = listOf(
                        Logo(type = "PICTORIAL", url = "http://www.goole.com/images/orange.png")
                    )
                )
            ),
            testUserIds = listOf(ACCOUNT_ID),
            financialInstitutions = listOf(
                FinancialInstitution(
                    code = PaymentMethodProvider.WAF.name,
                    name = "Women Access Finance",
                    type = FinancialInstitutionType.MICRO_FINANCE.name,
                    countries = listOf("CM"),
                    logos = listOf(
                        Logo(type = "PICTORIAL", url = "http://www.goole.com/images/waf.png")
                    )
                )
            ),
            creditCardTypes = listOf(
                CreditCardType(
                    code = PaymentMethodProvider.VISA.name,
                    name = "Visa",
                    countries = listOf("CM"),
                    logos = listOf(
                        Logo(type = "PICTORIAL", url = "http://www.goole.com/images/visa.png")
                    )
                ),
                CreditCardType(
                    code = PaymentMethodProvider.MASTERCARD.name,
                    name = "Mastercard",
                    countries = listOf("CM"),
                    logos = listOf(
                        Logo(type = "PICTORIAL", url = "http://www.goole.com/images/mastercard.png")
                    )
                )
            )
        )
        doReturn(GetTenantResponse(tenant)).whenever(tenantApi).getTenant(any())

        val category = Category(
            id = 1000,
            title = "Marketing"
        )
        user = createAccount(false, category)
        doReturn(GetAccountResponse(user)).whenever(accountApi).getAccount(any())
        doReturn(GetCategoryResponse(category)).whenever(accountApi).getCategory(any())

        rest = createResTemplate()
    }

    protected fun createAccount(
        business: Boolean = false,
        category: Category? = null,
        hasStore: Boolean = false,
        superUser: Boolean = false
    ) =
        Account(
            id = ACCOUNT_ID,
            displayName = "Ray Sponsible",
            country = "CM",
            cityId = 2225940L,
            language = "en",
            status = "ACTIVE",
            phone = Phone(
                id = 1,
                number = "+1237666666666",
                country = "CM"
            ),
            business = business,
            website = "https://www.google.ca",
            biography = "This is my bio",
            category = category,
            timezoneId = "Africa/Douala",
            whatsapp = "+1237666666666",
            pictureUrl = "http://img.com/1.png",
            email = "foo@bar.com",
            facebookId = "ray.sponsible",
            twitterId = "ray-sponsible",
            instagramId = "ray",
            hasStore = hasStore,
            superUser = superUser
        )

    private fun createResTemplate(
        scope: List<String> = listOf(
            "user-read",
            "user-manage",
            "payment-method-manage",
            "payment-method-read",
            "payment-manage",
            "payment-read",
            "tenant-read"
        ),
        subjectId: Long = ACCOUNT_ID,
        subjectType: SubjectType = USER
    ): RestTemplate {
        val rest = RestTemplate()
        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = subjectId.toString(),
                name = ACCOUNT_NAME,
                subjectType = subjectType,
                scope = scope,
                keyProvider = TestRSAKeyProvider(),
                admin = false
            ).build()
        )

        rest.interceptors.add(SpringTracingRequestInterceptor(tracingContext))
        rest.interceptors.add(SpringAuthorizationRequestInterceptor(tokenProvider))
        rest.interceptors.add(LanguageClientHttpRequestInterceptor())
        return rest
    }

    protected fun assertEndpointEquals(expectedPath: String, url: String) {
        val request = emptyMap<String, String>()
        val response = rest.postForEntity(url, request, Map::class.java)

        assertJsonEquals(expectedPath, response.body)
    }

    protected fun assertJsonEquals(expectedPath: String, value: Any?) {
        val input = AbstractShellEndpointTest::class.java.getResourceAsStream(expectedPath)
        val expected = mapper.readValue(input, Any::class.java)

        val writer = mapper.writerWithDefaultPrettyPrinter()

        assertEquals(writer.writeValueAsString(expected).trimIndent(), writer.writeValueAsString(value).trimIndent())
    }

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key

    protected fun createFeignException(errorCode: String, downstreamError: ErrorCode? = null) = FeignException.Conflict(
        "",
        Request.create(
            Request.HttpMethod.POST,
            "https://www.google.ca",
            emptyMap(),
            "".toByteArray(),
            Charset.defaultCharset(),
            RequestTemplate()
        ),
        """
            {
                "error":{
                    "code": "$errorCode",
                    "downstreamCode": "$downstreamError"
                }
            }
        """.trimIndent().toByteArray(),
        emptyMap()
    )
}