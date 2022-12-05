package com.wutsi.application

import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.PaymentMethodSummary
import com.wutsi.checkout.manager.dto.PaymentProviderSummary
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.PaymentMethodType
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
        price: Long = 15000
    ) = ProductSummary(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        status = if (published) "PUBLISHED" else "DRAFT",
        price = price
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
        published: Boolean = true
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
        status = if (published) "PUBLISHED" else "DRAFT"
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
            status = status.name
        )

    fun createBusiness(id: Long, accountId: Long) = Business(
        id = id,
        accountId = accountId,
        country = "CM"
    )
}
