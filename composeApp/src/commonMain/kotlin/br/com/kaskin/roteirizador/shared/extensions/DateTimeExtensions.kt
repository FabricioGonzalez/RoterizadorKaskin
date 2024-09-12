package br.com.kaskin.roteirizador.shared.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime


fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate.Companion.now(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun LocalDateTime.format(): String {
    return LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
        char('T')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }.format(this)
}

fun LocalDateTime.formatView(): String {
    return LocalDateTime.Format {
        dayOfMonth()
        char('-')
        monthNumber()
        char('-')
        year()
        /*char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()*/
    }.format(this)
}


fun Long.toDateTime(): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC)
}