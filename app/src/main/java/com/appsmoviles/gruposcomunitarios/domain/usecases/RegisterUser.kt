package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.graphics.Bitmap
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*

interface RegisterUserUseCase {
    fun registerUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        bitmap: Bitmap? = null,
    ): Flow<Res<Nothing>>
}

class RegisterUserUseCaseImp(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : RegisterUserUseCase {
    override fun registerUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        bitmap: Bitmap?
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())

        var imageUrl: String? = null
        if (bitmap != null) {
            val imageUrlRes =
                storageRepository.loadImageToStorage(username, "${UUID.randomUUID()}.jpeg", bitmap)
                    .first()

            if (imageUrlRes !is Res.Success) {
                emit(Res.Error(imageUrlRes.message))
                return@flow
            }

            imageUrl = imageUrlRes.data!!
        }

        val resAuth = userRepository.registerUserAuth(email, password).first()

        if (resAuth !is Res.Error) {
            emit(resAuth)
        }

        val user = User(
            username = username,
            name = name,
            surname = surname,
            email = email,
            photo = imageUrl
        )

        val resFirestore = userRepository.registerUserFirestore(user).first()
        emit(resFirestore)
    }

}