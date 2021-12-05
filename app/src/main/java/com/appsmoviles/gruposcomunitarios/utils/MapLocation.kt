package com.appsmoviles.gruposcomunitarios.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapLocation(
    val latitude: Double,
    val longitude: Double
) : Parcelable