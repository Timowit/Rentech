package com.rentech.server

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val ZONA_WIB: ZoneId = ZoneId.of("Asia/Jakarta")
private val FORMAT_JAM = DateTimeFormatter.ofPattern("HH.mm")

/** "09.24" dalam WIB, sama dengan format jam di layar chat. */
fun jam(t: OffsetDateTime): String = t.atZoneSameInstant(ZONA_WIB).format(FORMAT_JAM)
