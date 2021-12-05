package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface RegisterUserUseCase {
    fun registerUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        photo: String? = null,
    ): Flow<Res<Nothing>>
}

class RegisterUserUseCaseImp(
    private val userRepository: UserRepository
) : RegisterUserUseCase {
    override fun registerUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        photo: String?
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val resAuth = userRepository.registerUserAuth(email, password).first()

        if (resAuth !is Res.Error) {
            emit(resAuth)
        }

        val user = User(
            username = username,
            name = name,
            surname = surname,
            email = email,
        )

        val resFirestore = userRepository.registerUserFirestore(user).first()
        emit(resFirestore)
    }

}