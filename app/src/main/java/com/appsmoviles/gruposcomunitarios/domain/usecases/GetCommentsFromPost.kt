package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetCommentsFromPostUseCase {
    fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>>
}

class GetCommentsFromPostUseCaseImp(
    private val postRepository: PostRepository
) : GetCommentsFromPostUseCase {
    override fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>> = flow {
        emit(Res.Loading())
        val commentsRes = postRepository.getComments(groupId, postId).first()
        emit(commentsRes)
    }
}