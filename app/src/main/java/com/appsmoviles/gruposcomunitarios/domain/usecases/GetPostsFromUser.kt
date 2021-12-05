package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.helpers.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetPostsFromUserUseCase {
    fun getPosts(username: String): Flow<Res<List<Post>>>
}

class GetPostsFromUserUseCaseImp(
    private val postRepository: PostRepository
) : GetPostsFromUserUseCase {
    override fun getPosts(username: String): Flow<Res<List<Post>>> = flow {
        emit(Res.Loading())
        val res = postRepository.getPostsFromUser(username, SortBy.CREATED_AT_DESCENDING).first()
        emit(res)
    }
}