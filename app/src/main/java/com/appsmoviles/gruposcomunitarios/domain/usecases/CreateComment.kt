package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

interface CreateCommentUseCase {
    fun createComment(
        groupId: String,
        postId: String,
        username: String,
        content: String,
        parentId: String? = null
    ): Flow<Res<Nothing>>
}

class CreateCommentUseCaseImp(
    private val postRepository: PostRepository
) : CreateCommentUseCase {
    override fun createComment(
        groupId: String,
        postId: String,
        username: String,
        content: String,
        parentId: String?
    ): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())

        val comment = PostComment(
            username = username,
            content = content,
            createdAt = Date(),
            likes = ArrayList(),
            parent = parentId
        )

        val res = postRepository.postComment(groupId, postId, comment).first()
        emit(res)
    }
}