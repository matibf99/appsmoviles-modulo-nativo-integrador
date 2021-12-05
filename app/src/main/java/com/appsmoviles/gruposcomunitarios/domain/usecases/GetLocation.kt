package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.location.Location
import com.appsmoviles.gruposcomunitarios.domain.repository.LocationRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

interface GetLocationUseCase {
    fun getLocation(): Flow<Res<Location>>
}

class GetLocationUseCaseImp(
    private val locationRepository: LocationRepository
) : GetLocationUseCase {
    override fun getLocation(): Flow<Res<Location>> = flow {
        emit(Res.Loading())

        locationRepository.getLocation().collect {
            emit(it)
        }
    }
}