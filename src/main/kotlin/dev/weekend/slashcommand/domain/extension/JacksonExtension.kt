package dev.weekend.slashcommand.domain.extension

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Jaeguk Cho
 */

object Jackson {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val mapper = ObjectMapper().apply {
        registerModules(
            KotlinModule.Builder()
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.StrictNullChecks, true)
                .build(),
            JavaTimeModule().apply {
                addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DATETIME_FORMATTER))
                addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DATETIME_FORMATTER))
                addSerializer(LocalDate::class.java, LocalDateSerializer(DATE_FORMATTER))
                addDeserializer(LocalDate::class.java, LocalDateDeserializer(DATE_FORMATTER))
            })
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
    }

    fun mapper() = mapper
}

fun Any.toJson(): String = Jackson.mapper().writeValueAsString(this)

inline fun <reified T> String?.toModelOrNull(): T? {
    return try {
        this?.let { Jackson.mapper().readValue(this, jacksonTypeRef<T>()) }
    } catch (e: Exception) {
        null
    }
}
