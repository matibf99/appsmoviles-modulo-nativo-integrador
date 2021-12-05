package com.appsmoviles.gruposcomunitarios.data

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.appsmoviles.gruposcomunitarios.domain.repository.LocationRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                if (locationResult == null) {
                    return
                }

                for (location: Location in locationResult.locations) {
                    if (location != null)
                        trySend(Res.Success(location))
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            channel.close()
        }
    }
}