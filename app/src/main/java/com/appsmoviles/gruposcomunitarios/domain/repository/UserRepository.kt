package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.User

interface UserRepository {
    suspend fun registerUser(email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun getUserInfo(): Result<User>
}