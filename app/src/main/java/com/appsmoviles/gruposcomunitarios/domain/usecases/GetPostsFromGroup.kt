package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetPostsFromGroupUseCase {
    fun getPosts(groupId: String, sortBy: SortBy): Flow<Res<List<Post>>>
}

class GetPostsFromGroupUseCaseImp(
    private val postRepository: PostRepository
) : GetPostsFromGroupUseCase {
    override fun getPosts(groupId: String, sortBy: SortBy): Flow<Res<List<Post>>> = flow {
        emit(Res.Loading())
        val postsRes = postRepository.getPosts(groupId, SortBy.CREATED_AT_DESCENDING).first()
        emit(postsRes)
    }
}