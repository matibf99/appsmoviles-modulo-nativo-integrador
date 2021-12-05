package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface LogInUserUseCase {
    fun logIn(email: String, password: String): Flow<Res<Nothing>>
}

class LogInUserUseCaseImp(
    private val userRepository: UserRepository
) : LogInUserUseCase {
    override fun logIn(email: String, password: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val res = userRepository.logIn(email, password).first()
        emit(res)
    }
}

