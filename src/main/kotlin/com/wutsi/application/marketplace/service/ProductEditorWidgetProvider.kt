package com.wutsi.application.marketplace.service

import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class ProductEditorWidgetProvider(
    private val regulationEngine: RegulationEngine,
    private val messages: MessageSource
) {
    fun get(name: String, product: Product): WidgetAware =
        when (name) {
            "title" -> get(name, product.title)
            "summary" -> get(name, product.summary)
            "description" -> get(name, product.description)
            "quantity" -> get(name, product.quantity)
            "price" -> get(name, product.price)
            else -> throw IllegalStateException("Not supported: $name")
        }

    fun get(name: String, defaultValue: Any?, country: String? = null): WidgetAware =
        when (name) {
            "title" -> getInputWidget(defaultValue, 100, required = true)
            "summary" -> getInputWidget(defaultValue?.toString(), 160, maxLines = 2)
            "description" -> getInputWidget(defaultValue, 1000, maxLines = 5)
            "quantity" -> getInputWidget(defaultValue, type = InputType.Number, decimal = false)
            "price" -> getInputWidget(
                defaultValue,
                maxlength = 30,
                decimal = country?.let {
                    regulationEngine.country(country).monetaryFormat.indexOf(".") >= -1
                } ?: true
            )
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun getInputWidget(
        value: Any?,
        maxlength: Int? = null,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false,
        decimal: Boolean? = null
    ) =
        Input(
            name = "value",
            value = value?.toString(),
            type = type,
            maxLength = maxlength,
            maxLines = maxLines,
            required = required,
            inputFormatterRegex = if (decimal == false) "[0-9]" else null
        )

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, getLocale())
}
