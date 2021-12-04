package com.appsmoviles.gruposcomunitarios.domain.repository

import android.location.Location
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocation() : Flow<Res<Location>>
}