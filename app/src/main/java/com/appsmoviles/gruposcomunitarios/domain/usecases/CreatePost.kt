package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.graphics.Bitmap
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*

interface CreatePostUseCase {
    fun createPost(
        groupId: String,
        title: String,
        content: String,
        imageBitmap: Bitmap?
    ) : Flow<Res<Nothing>>
}

class CreatePostUseCaseImp(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : CreatePostUseCase {
    override fun createPost(
        groupId: String,
        title: String,
        content: String,
        imageBitmap: Bitmap?
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())

        val userIdRes = userRepository.getCurrentUserDocumentId().first()

        if (userIdRes !is Res.Success) {
            emit(Res.Error(userIdRes.message))
            return@flow
        }

        var imageUrl: String? = null
        if (imageBitmap != null) {
            val imageUrlRes = storageRepository.loadImageToStorage(groupId, "${UUID.randomUUID()}-$title.jpeg", imageBitmap).first()

            if (imageUrlRes !is Res.Success) {
                emit(Res.Error(imageUrlRes.message))
                return@flow
            }

            imageUrl = imageUrlRes.data!!
        }

        val post = Post(
            title = title,
            content = content,
            commentsCount = 0,
            photo = imageUrl,
            createdAt = Date(),
            createdBy = userIdRes.data
        )

        val postRes = postRepository.createPost(groupId, post).first()
        emit(postRes)
    }

}