package com.appsmoviles.gruposcomunitarios.data

import android.annotation.SuppressLint
import android.location.Location
import com.appsmoviles.gruposcomunitarios.domain.repository.LocationRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
@SuppressLint("MissingPermission")
class LocationRepositoryImp(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    override fun getLocation(): Flow<Res<Location>> = callbackFlow {
        trySend(Res.Loading())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                trySend(Res.Success(location))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
        awaitClose { channel.close() }
    }
}