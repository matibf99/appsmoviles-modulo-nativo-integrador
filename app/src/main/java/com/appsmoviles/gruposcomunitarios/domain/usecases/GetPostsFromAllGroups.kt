package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetPostsFromAllGroupsUseCase {
    fun getPosts(sortBy: SortBy): Flow<Res<List<Post>>>
}

class GetPostsFromAllGroupsUseCaseImp(
    private val postsRepository: PostRepository
) : GetPostsFromAllGroupsUseCase {
    override fun getPosts(sortBy: SortBy): Flow<Res<List<Post>>> = flow {
        emit(Res.Loading())

        val postsRes = postsRepository.getPostsFromAllGroups(sortBy).first()
        emit(postsRes)
    }
}