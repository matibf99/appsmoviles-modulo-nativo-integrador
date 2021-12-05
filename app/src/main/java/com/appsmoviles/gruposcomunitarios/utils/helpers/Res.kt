package com.appsmoviles.gruposcomunitarios.utils.helpers

sealed class Res<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null) : Res<T>(data)
    class Loading<T>(data: T? = null) : Res<T>(data)
    class Error<T>(message: String? = null, data: T? = null) : Res<T>(data, message)
}