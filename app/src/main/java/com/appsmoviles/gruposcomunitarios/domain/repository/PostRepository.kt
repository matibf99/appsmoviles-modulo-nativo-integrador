package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.helpers.SortBy
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun createPost(groupId: String, post: Post): Flow<Res<Nothing>>
    fun modifyPost(groupId: String, postId: String, post: Post): Flow<Res<Nothing>>
    fun deletePost(groupId: String, postId: String): Flow<Res<Nothing>>
    fun getPost(groupId: String, postId: String): Flow<Res<Post>>
    fun getPosts(groupId: String, sortBy: SortBy): Flow<Res<List<Post>>>
    fun getPostsFromAllGroups(sortBy: SortBy): Flow<Res<List<Post>>>
    fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>>
    fun postComment(groupId: String, postId: String, comment: PostComment): Flow<Res<Nothing>>
    fun modifyComment(groupId: String, postId: String, commentId: String, comment: PostComment): Flow<Res<Nothing>>
    fun likePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>>
    fun unlikePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>>
    fun likeComment(groupId: String, postId: String, commentId: String, username: String): Flow<Res<Nothing>>
    fun unlikeComment(groupId: String, postId: String, commentId: String, username: String): Flow<Res<Nothing>>
}