package com.friendscrm.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object DateUtils {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

    fun today(): LocalDate = LocalDate.now()

    fun format(localDate: LocalDate?): String? = localDate?.format(formatter)

    fun parse(date: String?): LocalDate? = date?.let { LocalDate.parse(it, formatter) }
}
