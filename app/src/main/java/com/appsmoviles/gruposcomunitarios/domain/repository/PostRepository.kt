package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment

interface PostRepository {
    fun createPost(post: Post): Result<Nothing>
    fun modifyPost(postId: String, post: Post): Result<Nothing>
    fun deletePost(postId: String): Result<Nothing>
    fun getPost(postId: String): Result<Post>
    fun getPosts(groupId: String, sortBy: Int): Result<List<Post>>
    fun postComment(groupId: String, comment: PostComment): Result<Nothing>
    fun modifyComment(commentaryId: String, comment: PostComment): Result<Nothing>
}