package com.rentech.server

import java.util.Locale

/** 3075000 → "Rp3.075.000", sama dengan rupiah() di aplikasi. */
fun rupiah(nilai: Long): String = "Rp" + String.format(Locale.US, "%,d", nilai).replace(',', '.')
