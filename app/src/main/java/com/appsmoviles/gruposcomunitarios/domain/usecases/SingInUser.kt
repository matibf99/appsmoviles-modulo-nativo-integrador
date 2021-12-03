package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface SingInUserCase {
    fun signIn(email: String, password: String): Flow<Res<Nothing>>
}

class SingInUserCaseImp (
    private val userRepository: UserRepository
    ): SingInUserCase {
    override fun signIn(email: String, password: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val res = userRepository.signIn(email, password).first()
        emit(res)
    }
    }

