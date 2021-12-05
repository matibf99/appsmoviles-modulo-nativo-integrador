package com.appsmoviles.gruposcomunitarios.utils.time

import java.util.*

fun Date.getTimeAgo(): String {
    val calendar = Calendar.getInstance()
    calendar.time = this

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val currentCalendar = Calendar.getInstance()

    val currentYear = currentCalendar.get(Calendar.YEAR)
    val currentMonth = currentCalendar.get(Calendar.MONTH)
    val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = currentCalendar.get(Calendar.MINUTE)

    return if (year < currentYear ) {
        val interval = currentYear - year
        "${interval}y"
    } else if (month < currentMonth) {
        val interval = currentMonth - month
        "${interval}m"
    } else  if (day < currentDay) {
        val interval = currentDay - day
        "${interval}d"
    } else if (hour < currentHour) {
        val interval = currentHour - hour
        "${interval}h"
    } else if (minute < currentMinute) {
        val interval = currentMinute - minute
        if (interval == 1) "$interval min" else "$interval mins"
    } else {
        "a moment ago"
    }
}