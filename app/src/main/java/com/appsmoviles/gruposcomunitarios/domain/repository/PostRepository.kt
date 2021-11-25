package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun createPost(groupId: String, post: Post): Flow<Res<Nothing>>
    fun modifyPost(groupId: String, postId: String, post: Post): Flow<Res<Nothing>>
    fun deletePost(groupId: String, postId: String): Flow<Res<Nothing>>
    fun getPost(groupId: String, postId: String): Flow<Res<Post>>
    fun getPosts(groupId: String, sortBy: Int): Flow<Res<List<Post>>>
    fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>>
    fun postComment(groupId: String, postId: String, comment: PostComment): Flow<Res<Nothing>>
    fun modifyComment(groupId: String, postId: String, commentId: String, comment: PostComment): Flow<Res<Nothing>>
}