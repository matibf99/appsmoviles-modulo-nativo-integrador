package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUserAuth(email: String, password: String): Flow<Res<Nothing>>
    suspend fun registerUserFirestore(user: User): Flow<Res<Nothing>>
    suspend fun logIn(email: String, password: String): Flow<Res<Nothing>>
    suspend fun getCurrentUserInfo(): Flow<Res<User>>
    fun getCurrentUserDocumentId(): String?
    fun logOut()
}