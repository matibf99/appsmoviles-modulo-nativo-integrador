package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface RegisterUserCase {
    fun registerUser(user: User, password: String): Flow<Res<Nothing>>
}

class RegisterUserImp (
    private  val userRepository: UserRepository
        ): RegisterUserCase {
    override fun registerUser(user: User, password: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val res = userRepository.registerUser(user, password).first()
        emit(res)
    }
        }