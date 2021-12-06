package com.appsmoviles.gruposcomunitarios.utils.time

import android.content.Context
import com.appsmoviles.gruposcomunitarios.R
import java.util.*

fun Date.getTimeAgo(context: Context): String {
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
        if (interval == 1) "$interval ${context.getString(R.string.time_ago_year)}" else "$interval ${context.getString(R.string.time_ago_years)}"
    } else if (month < currentMonth) {
        val interval = currentMonth - month
        if (interval == 1) "$interval ${context.getString(R.string.time_ago_month)}" else "$interval ${context.getString(R.string.time_ago_months)}"
    } else  if (day < currentDay) {
        val interval = currentDay - day
        if (interval == 1) "$interval ${context.getString(R.string.time_ago_day)}" else "$interval ${context.getString(R.string.time_ago_days)}"
    } else if (hour < currentHour) {
        val interval = currentHour - hour
        if (interval == 1) "$interval ${context.getString(R.string.time_ago_hour)}" else "$interval ${context.getString(R.string.time_ago_hour)}"
    } else if (minute < currentMinute) {
        val interval = currentMinute - minute
        if (interval == 1) "$interval ${context.getString(R.string.time_ago_minute)}" else "$interval ${context.getString(R.string.time_ago_minutes)}"
    } else {
        context.getString(R.string.time_ago_moment)
    }
}