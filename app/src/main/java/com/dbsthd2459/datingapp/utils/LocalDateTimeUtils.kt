package com.dbsthd2459.datingapp.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeUtils {

    companion object {

        // Long -> LocalDateTime
        fun Long.toLocalDateTime(): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
        }

        // LocalDateTime -> Long
        fun LocalDateTime.toTimestamp(): Long {
            return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

    }

}