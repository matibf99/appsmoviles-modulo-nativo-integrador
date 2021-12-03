package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface UnlikePostUseCase {
    fun unlikePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>>
}

class UnlikePostUseCaseImp(
    val postRepository: PostRepository
) : UnlikePostUseCase {
    override fun unlikePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>> = flow{
        emit(Res.Loading())

        val likeRes = postRepository.unlikePost(groupId, postId, userId).first()
        emit(likeRes)
    }
}