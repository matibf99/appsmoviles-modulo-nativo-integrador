package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository

interface LogOutUserUseCase {
    fun logOut()
}

class LogOutUserUseCaseImp(private val userRepository: UserRepository) : LogOutUserUseCase {
    override fun logOut() {
        userRepository.logOut()
    }
}