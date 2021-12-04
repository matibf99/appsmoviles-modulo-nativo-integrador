package com.appsmoviles.gruposcomunitarios.presentation.postcomments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetCommentsFromPostUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.LikeCommentUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnlikeCommentUseCase
import com.appsmoviles.gruposcomunitarios.presentation.post.PostViewModel
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PostCommentsStatus(val message: String? = null) {
    object Success : PostCommentsStatus()
    object Loading : PostCommentsStatus()
    class Error(message: String?) : PostCommentsStatus(message)
}

@HiltViewModel
class PostCommentsViewModel @Inject constructor(
    private val likeCommentUseCase: LikeCommentUseCase,
    private val unlikeCommentUseCase: UnlikeCommentUseCase,
    private val getCommentsFromPostUseCase: GetCommentsFromPostUseCase
) : ViewModel() {

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post> get() = _post

    private val _parent: MutableLiveData<PostComment> = MutableLiveData()
    val parent: LiveData<PostComment> get() = _parent

    private val _comments: MutableLiveData<List<PostComment>> = MutableLiveData()
    val comments: LiveData<List<PostComment>> get() = _comments

    private val _commentsStatus: MutableLiveData<PostCommentsStatus> = MutableLiveData()
    val commentsStatus: LiveData<PostCommentsStatus> get() = _commentsStatus

    fun setPost(post: Post) {
        _post.value = post
        loadComments()
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

        viewModelScope.launch(Dispatchers.IO) {
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

    fun loadComments() {
        viewModelScope.launch(Dispatchers.IO) {
            getCommentsFromPostUseCase
                .getComments(post.value!!.groupId!!, post.value!!.documentId!!)
                .collect {
                    when(it) {
                        is Res.Success -> {
                            val comments = it.data!!

                            if (parent.value != null)
                                _parent.postValue(comments.first { item -> item.documentId == parent.value!!.documentId!! })

                            _comments.postValue(comments.filter { item -> item.parent == parent.value!!.documentId})
                            _commentsStatus.postValue(PostCommentsStatus.Success)
                        }
                        is Res.Loading -> _commentsStatus.postValue(PostCommentsStatus.Loading)
                        is Res.Error -> _commentsStatus.postValue(PostCommentsStatus.Error(it.message))
                    }
                }
        }
    }

    companion object {
        private const val TAG = "PostCommentsViewModel"
    }
}