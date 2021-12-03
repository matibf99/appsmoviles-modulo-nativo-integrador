package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


interface GetUserRegisteredUseCase {
    fun registeredUser(): Boolean
}

class GetUserRegisteredImp (
    private val userRepository: UserRepository
    ) : GetUserRegisteredUseCase {
    override fun registeredUser(): Boolean {
        return userRepository.registeredUser()
    }
}