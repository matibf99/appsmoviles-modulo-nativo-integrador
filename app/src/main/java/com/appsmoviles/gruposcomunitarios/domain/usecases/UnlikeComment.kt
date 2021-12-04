package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface UnlikeCommentUseCase {
    fun unlikeComment(groupId: String, postId: String, commentId: String, username: String): Flow<Res<Nothing>>
}

class UnlikeCommentUseCaseImp(
    private val postRepository: PostRepository
) : UnlikeCommentUseCase {
    override fun unlikeComment(
        groupId: String,
        postId: String,
        commentId: String,
        username: String
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val res = postRepository.unlikeComment(groupId, postId, commentId, username).first()
        emit(res)
    }
}