package com.appsmoviles.gruposcomunitarios.utils

sealed class Res<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null) : Res<T>(data)
    class Loading<T>(data: T? = null) : Res<T>(data)
    class Error<T>(message: String?, data: T? = null) : Res<T>(data, message)
}