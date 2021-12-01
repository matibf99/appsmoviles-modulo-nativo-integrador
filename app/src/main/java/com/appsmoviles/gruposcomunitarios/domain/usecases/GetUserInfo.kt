package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetUserInfoUseCase {
    fun getUserInfo(): Flow<Res<User>>
}

class GetUserInfoUseCaseImp(
     private val userRepository: UserRepository
) : GetUserInfoUseCase {
    override fun getUserInfo(): Flow<Res<User>> = flow {
        emit(Res.Loading())
        val res = userRepository.getCurrentUserInfo().first()
        emit(res)
    }
}