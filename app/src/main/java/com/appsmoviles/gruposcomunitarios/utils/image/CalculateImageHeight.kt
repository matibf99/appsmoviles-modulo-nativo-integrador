package com.appsmoviles.gruposcomunitarios.utils

import android.content.res.Resources
import android.util.DisplayMetrics

fun calculateImageHeight(): Int {
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return (displayMetrics.widthPixels / 1.6).toInt()
}