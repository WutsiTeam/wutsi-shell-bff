package com.wutsi.application

import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.BusinessSummary
import com.wutsi.checkout.manager.dto.Discount
import com.wutsi.checkout.manager.dto.Order
import com.wutsi.checkout.manager.dto.OrderItem
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.PaymentMethod
import com.wutsi.checkout.manager.dto.PaymentMethodSummary
import com.wutsi.checkout.manager.dto.PaymentProviderSummary
import com.wutsi.checkout.manager.dto.Transaction
import com.wutsi.checkout.manager.dto.TransactionSummary
import com.wutsi.enums.BusinessStatus
import com.wutsi.enums.ChannelType
import com.wutsi.enums.DeviceType
import com.wutsi.enums.DiscountType
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.PaymentMethodType
import com.wutsi.enums.ProductType
import com.wutsi.enums.TransactionType
import com.wutsi.marketplace.manager.dto.PictureSummary
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.marketplace.manager.dto.ProductSummary
import com.wutsi.marketplace.manager.dto.Store
import com.wutsi.marketplace.manager.dto.StoreSummary
import com.wutsi.membership.manager.dto.Category
import com.wutsi.membership.manager.dto.CategorySummary
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.Place
import com.wutsi.membership.manager.dto.PlaceSummary
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import java.time.OffsetDateTime
import java.time.ZoneOffset

object Fixtures {
    fun createMemberSummary() = MemberSummary()

    fun createMember(
        id: Long = -1,
        phoneNumber: String = "+237670000010",
        displayName: String = "Ray Sponsible",
        business: Boolean = false,
        storeId: Long? = null,
        businessId: Long? = null,
        country: String = "CM",
        superUser: Boolean = false
    ) = Member(
        id = id,
        active = true,
        phoneNumber = phoneNumber,
        business = business,
        storeId = storeId,
        businessId = businessId,
        country = country,
        email = "ray.sponsible@gmail.com",
        displayName = displayName,
        language = "en",
        pictureUrl = "https://www.img.com/100.png",
        superUser = superUser,
        biography = "This is a biography",
        city = Place(
            id = 111,
            name = "Yaounde",
            longName = "Yaounde, Cameroun"
        ),
        category = Category(
            id = 555,
            title = "Ads"
        )
    )

    fun createPlaceSummary(id: Long = -1, name: String = "Yaounde") = PlaceSummary(
        id = id,
        name = name
    )

    fun createCategorySummary(id: Long = -1, title: String = "Art") = CategorySummary(
        id = id,
        title = title
    )

    fun createProductSummary(
        id: Long = -1,
        title: String = "Product",
        thumbnailUrl: String? = null,
        published: Boolean = true,
        price: Long = 15000,
        type: ProductType = ProductType.PHYSICAL_PRODUCT
    ) = ProductSummary(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        status = if (published) "PUBLISHED" else "DRAFT",
        price = price,
        type = type.name
    )

    fun createProduct(
        id: Long = -1,
        storeId: Long = -1,
        title: String = "Product A",
        quantity: Int = 10,
        price: Long = 20000L,
        summary: String = "This is a summary",
        description: String = "This is a long description",
        pictures: List<PictureSummary> = emptyList(),
        published: Boolean = true,
        type: ProductType = ProductType.PHYSICAL_PRODUCT
    ) = Product(
        id = id,
        store = createStoreSummary(storeId),
        title = title,
        quantity = quantity,
        price = price,
        summary = summary,
        description = description,
        thumbnail = if (pictures.isEmpty()) null else pictures[0],
        pictures = pictures,
        status = if (published) "PUBLISHED" else "DRAFT",
        type = type.name
    )

    fun createPictureSummary(
        id: Long = -1,
        url: String = "http://www.google.com/1.png"
    ) = PictureSummary(
        id = id,
        url = url
    )

    fun createPictureSummaryList(size: Int): List<PictureSummary> {
        val pictures = mutableListOf<PictureSummary>()
        for (i in 0..size) {
            pictures.add(
                PictureSummary(
                    id = i.toLong(),
                    url = "https://img.com/$i.png"
                )
            )
        }
        return pictures
    }

    fun createStore(id: Long, accountId: Long) = Store(
        id = id,
        accountId = accountId
    )

    fun createStoreSummary(id: Long, accountId: Long = -1) = StoreSummary(
        id = id,
        accountId = accountId
    )

    fun createPaymentMethodSummary(
        token: String = "111",
        type: PaymentMethodType = PaymentMethodType.MOBILE_MONEY,
        number: String = "+23767000001",
        provider: String = "MTN"
    ) = PaymentMethodSummary(
        token = token,
        type = type.name,
        number = number,
        provider = createPaymentProviderSummary(provider, type)
    )

    fun createPaymentMethod(
        token: String = "111",
        type: PaymentMethodType = PaymentMethodType.MOBILE_MONEY,
        number: String = "+23767000001",
        provider: String = "MTN",
        country: String = "CM"
    ) = PaymentMethod(
        token = token,
        type = type.name,
        number = number,
        provider = createPaymentProviderSummary(provider, type),
        country = country
    )

    fun createPaymentProviderSummary(name: String = "MTN", type: PaymentMethodType = PaymentMethodType.MOBILE_MONEY) =
        PaymentProviderSummary(
            name = name,
            code = name,
            type = type.name,
            logoUrl = "https://img.com/$name.png"
        )

    fun createOrderSummary(id: String, totalPrice: Long = 15000, status: OrderStatus = OrderStatus.OPENED) =
        OrderSummary(
            id = id,
            totalPrice = totalPrice,
            status = status.name,
            created = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC)
        )

    fun createBusiness(id: Long, accountId: Long) = Business(
        id = id,
        accountId = accountId,
        country = "CM",
        balance = 30000
    )

    fun createOrder(
        id: String,
        businessId: Long = -1,
        merchantId: Long = -1,
        totalPrice: Long = 100000L,
        status: OrderStatus = OrderStatus.OPENED
    ) = Order(
        id = id,
        business = createBusinessSummary(businessId, merchantId),
        totalPrice = totalPrice,
        totalDiscount = 20000,
        subTotalPrice = totalPrice + 20000,
        totalPaid = totalPrice,
        balance = 0,
        status = status.name,
        customerName = "Ray Sponsible",
        customerEmail = "ray.sponsible@gmail.com",
        deviceType = DeviceType.MOBILE.name,
        channelType = ChannelType.WEB.name,
        currency = "XAF",
        notes = "Yo man",
        deviceId = "4309403-43094039-43094309",
        discounts = listOf(
            Discount(
                code = "111",
                amount = 1000,
                rate = 0,
                type = DiscountType.DYNAMIC.name
            )
        ),
        items = listOf(
            OrderItem(
                productId = 999,
                quantity = 3,
                title = "This is a product",
                pictureUrl = "https://img.com/1.png",
                totalPrice = totalPrice,
                unitPrice = totalPrice / 3,
                subTotalPrice = totalPrice - 100,
                totalDiscount = 100,
                discounts = listOf(
                    Discount(
                        code = "111",
                        amount = 100,
                        rate = 0,
                        type = DiscountType.DYNAMIC.name
                    )
                )
            )
        ),
        created = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC),
        updated = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC),
        expires = OffsetDateTime.of(2100, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC),
        transactions = listOf(
            createTransactionSummary("11", TransactionType.CHARGE, Status.SUCCESSFUL, id),
            createTransactionSummary("11", TransactionType.CHARGE, Status.FAILED, id),
            createTransactionSummary("11", TransactionType.CHARGE, Status.PENDING, id)
        )
    )

    fun createBusinessSummary(
        id: Long,
        accountId: Long,
        balance: Long = 100000,
        currency: String = "XAF",
        country: String = "CM",
        status: BusinessStatus = BusinessStatus.ACTIVE
    ) = BusinessSummary(
        id = id,
        balance = balance,
        currency = currency,
        country = country,
        status = status.name,
        accountId = accountId
    )

    fun createTransactionSummary(
        id: String,
        type: TransactionType,
        status: Status = Status.SUCCESSFUL,
        orderId: String? = null
    ) = TransactionSummary(
        id = id,
        type = type.name,
        orderId = orderId,
        status = status.name,
        created = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC)
    )

    fun createTransaction(
        id: String,
        type: TransactionType,
        status: Status,
        orderId: String? = null,
        businessId: Long = -1,
        accountId: Long = -1,
        error: ErrorCode? = null
    ) = Transaction(
        id = id,
        type = type.name,
        orderId = orderId,
        status = status.name,
        description = "This is description",
        currency = "XAF",
        business = createBusinessSummary(businessId, accountId),
        email = "ray.sponsble@gmail.com",
        created = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC),
        updated = OffsetDateTime.of(2020, 1, 1, 10, 30, 0, 0, ZoneOffset.UTC),
        errorCode = error?.name,
        customerId = 1111L,
        paymentMethod = createPaymentMethodSummary(""),
        financialTransactionId = "1111-111",
        gatewayTransactionId = "2222-222",
        supplierErrorCode = "xyz",
        amount = 10500,
        fees = 500,
        net = 10000,
        gatewayFees = 250,
        gatewayType = GatewayType.FLUTTERWAVE.name
    )
}
