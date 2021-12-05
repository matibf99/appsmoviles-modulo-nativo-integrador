package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.helpers.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*

interface GetUpdatedPostsFromUserUseCase {
    fun getPosts(username: String, recentThan: Date): Flow<Res<List<Post>>>
}

class GetUpdatedPostsFromUserUseCaseImp(
    private val postRepository: PostRepository
) : GetUpdatedPostsFromUserUseCase {
    override fun getPosts(username: String, recentThan: Date): Flow<Res<List<Post>>> = flow {
        emit(Res.Loading())

        val res = postRepository.getUpdatedPostsFromUser(
            username,
            recentThan,
            SortBy.CREATED_AT_ASCENDING
        ).first()
        emit(res)
    }
}