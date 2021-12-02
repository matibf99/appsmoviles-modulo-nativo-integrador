package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.graphics.Bitmap
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

interface CreateGroupUseCase {
    fun createGroup(name: String, description: String, tags: String, imageBitmap: Bitmap): Flow<Res<Nothing>>
}

class CreateGroupUseCaseImp(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : CreateGroupUseCase {
    override fun createGroup(name: String, description: String, tags: String, imageBitmap: Bitmap): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())

        val userIdRes = userRepository.getCurrentUserDocumentId().first()

        if (userIdRes !is Res.Success) {
            emit(Res.Error(userIdRes.message))
            return@flow
        }

        val imageUrlRes = storageRepository.loadImageToStorage(userIdRes.data!!, name, imageBitmap).first()

        if (imageUrlRes !is Res.Success) {
            emit(Res.Error(imageUrlRes.message))
            return@flow
        }

        val group = Group(
            name = name,
            description = description,
            tags = tags.lowercase().replace("\\s*,\\s*".toRegex(), ",").split(",").toList(),
            photo = imageUrlRes.data,
            createdBy = userIdRes.data,
            createdAt = Date(),
            moderators = ArrayList()
        )

        val groupRes = groupRepository.createGroup(group).first()
        emit(groupRes)
    }

}