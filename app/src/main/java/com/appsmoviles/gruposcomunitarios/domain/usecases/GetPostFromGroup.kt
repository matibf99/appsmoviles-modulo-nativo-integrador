package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetPostFromGroupUseCase {
    fun getGroup(groupId: String, postId: String): Flow<Res<Post>>
}

class GetPostFromGroupUseCaseImp(
    private val postRepository: PostRepository
) : GetPostFromGroupUseCase {
    override fun getGroup(groupId: String, postId: String): Flow<Res<Post>> = flow {
        emit(Res.Loading())
        val res = postRepository.getPost(groupId, postId).first()
        emit(res)
    }
}