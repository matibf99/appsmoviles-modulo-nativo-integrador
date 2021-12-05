package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface LikePostUseCase {
    fun likePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>>
}

class LikePostUseCaseImp(
    val postRepository: PostRepository
) : LikePostUseCase {
    override fun likePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>> = flow{
        emit(Res.Loading())
        val likeRes = postRepository.likePost(groupId, postId, userId).first()
        emit(likeRes)
    }
}