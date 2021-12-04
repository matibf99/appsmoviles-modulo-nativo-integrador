package com.appsmoviles.gruposcomunitarios.presentation.postcomments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.usecases.LikeCommentUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnlikeCommentUseCase
import com.appsmoviles.gruposcomunitarios.presentation.post.PostViewModel
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCommentsViewModel @Inject constructor(
    private val likeCommentUseCase: LikeCommentUseCase,
    private val unlikeCommentUseCase: UnlikeCommentUseCase
) : ViewModel() {

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post> get() = _post

    private val _parent: MutableLiveData<PostComment> = MutableLiveData()
    val parent: LiveData<PostComment> get() = _parent

    private val _comments: MutableLiveData<List<PostComment>> = MutableLiveData()
    val comments: LiveData<List<PostComment>> get() = _comments

    fun setPost(post: Post) {
        _post.value = post
    }

    fun setParent(postComment: PostComment) {
        _parent.value = postComment
    }

    fun setComments(comments: List<PostComment>) {
        _comments.value = comments
    }

    fun likeChildComment(position: Int, username: String) {
        likeComment(
            comments.value!![position],
            username
        )
    }

    fun likeParentComment(username: String) {
        likeComment(parent.value!!, username)
    }

    private fun likeComment(comment: PostComment, username: String) {
        val likePost: Boolean

        if (comment.likes?.contains(username) == true) {
            comment.likes = comment.likes?.filter { it != username }
            likePost = false
        }
        else {
            val likes = comment.likes!!.toMutableList()
            likes.add(username)
            comment.likes = likes
            likePost = true
        }

        viewModelScope.launch {
            val res = if (likePost) likeCommentUseCase.likeComment(
                post.value?.groupId!!,
                post.value?.documentId!!,
                comment.documentId!!,
                username
            )
            else unlikeCommentUseCase.unlikeComment(
                post.value?.groupId!!,
                post.value?.documentId!!,
                comment.documentId!!,
                username
            )

            res.collect {
                when(it) {
                    is Res.Success -> Log.d(TAG, "likeComment: like comment success")
                    is Res.Loading -> Log.d(TAG, "likeComment: loading like comment")
                    is Res.Error -> Log.d(TAG, "likeComment: loading like error")
                }
            }
        }
    }

    companion object {
        private const val TAG = "PostCommentsViewModel"
    }
}