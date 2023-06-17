package com.illegal.weathering

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convert(kelvin: Double?): String {
        return (kelvin?.minus(273.15)).toString()
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(UTC: Long): String {
        val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(UTC), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return date.format(formatter)
}