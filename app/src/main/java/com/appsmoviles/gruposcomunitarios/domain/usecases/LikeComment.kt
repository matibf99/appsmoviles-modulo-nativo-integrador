package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface LikeCommentUseCase {
    fun likeComment(groupId: String, postId: String, commentId: String, username: String): Flow<Res<Nothing>>
}

class LikeCommentUseCaseImp(
    private val postRepository: PostRepository
) : LikeCommentUseCase {
    override fun likeComment(
        groupId: String,
        postId: String,
        commentId: String,
        username: String
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val res = postRepository.likeComment(groupId, postId, commentId, username).first()
        emit(res)
    }

}