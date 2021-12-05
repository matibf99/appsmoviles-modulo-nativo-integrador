package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.graphics.Bitmap
import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

interface CreatePostUseCase {
    fun createPost(
        username: String,
        groupId: String,
        groupName: String,
        title: String,
        content: String,
        imageBitmap: Bitmap?,
        latitude: Double? = null,
        longitude: Double? = null,
    ) : Flow<Res<Nothing>>
}

class CreatePostUseCaseImp(
    private val postRepository: PostRepository,
    private val storageRepository: StorageRepository
) : CreatePostUseCase {
    override fun createPost(
        username: String,
        groupId: String,
        groupName: String,
        title: String,
        content: String,
        imageBitmap: Bitmap?,
        latitude: Double?,
        longitude: Double?,
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())

        var imageUrl: String? = null
        if (imageBitmap != null) {
            val imageUrlRes = storageRepository.loadImageToStorage(groupId, "${UUID.randomUUID()}-$title.jpeg", imageBitmap).first()

            if (imageUrlRes !is Res.Success) {
                emit(Res.Error(imageUrlRes.message))
                return@flow
            }

            imageUrl = imageUrlRes.data!!
        }

        Log.d("CreatePost", "createPost: latitude: $latitude")
        Log.d("CreatePost", "createPost: longitude: $longitude")

        val post = Post(
            title = title,
            content = content,
            commentsCount = 0,
            photo = imageUrl,
            createdBy = username,
            groupId = groupId,
            groupName = groupName,
            likes = ArrayList(),
            latitude = latitude?.toString(),
            longitude = longitude?.toString()
        )

        val res = postRepository.createPost(groupId, post).first()
        emit(res)
    }

}