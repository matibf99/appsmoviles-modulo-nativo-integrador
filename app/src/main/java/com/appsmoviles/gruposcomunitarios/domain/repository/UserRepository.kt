package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(user: User, password: String): Flow<Res<Nothing>>
    suspend fun signIn(email: String, password: String): Flow<Res<Nothing>>
    suspend fun getCurrentUserInfo(): Flow<Res<User>>
    suspend fun getCurrentUserDocumentId(): Flow<Res<String>>
}