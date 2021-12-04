package com.appsmoviles.gruposcomunitarios.presentation.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.usecases.*
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PostCommentsStatus(val message: String? = null) {
    object Success : PostCommentsStatus()
    object Loading : PostCommentsStatus()
    class Error(message: String?) : PostCommentsStatus(message)
}

sealed class PostStatus(val message: String? = null) {
    object Success : PostStatus()
    object Loading : PostStatus()
    class Error(message: String?) : PostStatus(message)
}

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostFromGroupUseCase: GetPostFromGroupUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getCommentsFromPostUseCase: GetCommentsFromPostUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val unlikeCommentUseCase: UnlikeCommentUseCase
) : ViewModel() {

    private val _postStatus: MutableLiveData<PostStatus> = MutableLiveData(PostStatus.Success)
    val postStatus: LiveData<PostStatus> get() = _postStatus

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post> get()= _post

    private val _commentsStatus: MutableLiveData<PostCommentsStatus> = MutableLiveData()
    val commentsStatus: LiveData<PostCommentsStatus> get() = _commentsStatus

    private val _parentComments: MutableLiveData<List<PostComment>> = MutableLiveData(ArrayList())
    val parentComments: LiveData<List<PostComment>> get() = _parentComments

    private val _allComments: MutableLiveData<List<PostComment>> = MutableLiveData(ArrayList())
    val allComments: LiveData<List<PostComment>> get() = _allComments

    fun setPost(post: Post) {
        _post.value = post
        refresh()
    }

    fun refresh() {
        getPost()
        getComments()
    }

    fun getPost() {
        viewModelScope.launch {
            getPostFromGroupUseCase.getGroup(post.value!!.groupId!!, post.value!!.documentId!!).collect {
                when(it) {
                    is Res.Success -> {
                        _post.postValue(it.data!!)
                        _postStatus.postValue(PostStatus.Success)
                    }
                    is Res.Loading -> _postStatus.postValue(PostStatus.Loading)
                    is Res.Error -> _postStatus.postValue(PostStatus.Error(it.message))
                }
            }
        }
    }

    fun getComments() {
        viewModelScope.launch {
            getCommentsFromPostUseCase.getComments(post.value!!.groupId!!, post.value!!.documentId!!).collect {
                when(it) {
                    is Res.Success -> {
                        _allComments.postValue(it.data!!)
                        _parentComments.postValue(it.data.filter { item -> item.parent == null })

                        _commentsStatus.postValue(PostCommentsStatus.Success)
                    }
                    is Res.Loading -> _commentsStatus.postValue(PostCommentsStatus.Loading)
                    is Res.Error -> _commentsStatus.postValue(PostCommentsStatus.Error(it.message))
                }
            }
        }
    }

    fun likePost(username: String) {
        val p = post.value!!
        Log.d(TAG, "likePost: $p")
        val likePost: Boolean

        if (p.likes?.contains(username) == true) {
            p.likes = p.likes?.filter { it != username }
            likePost = false
        }
        else {
            val likes = p.likes!!.toMutableList()
            likes.add(username)
            p.likes = likes
            likePost = true
        }

        viewModelScope.launch {
            val res = if (likePost) likePostUseCase.likePost(p.groupId!!, p.documentId!!, username)
            else unlikePostUseCase.unlikePost(p.groupId!!, p.documentId!!, username)

            res.collect {
                when(it) {
                    is Res.Success -> Log.d(TAG, "likePost: success")
                    is Res.Loading -> Log.d(TAG, "likePost: loading")
                    is Res.Error -> Log.d(TAG, "likePost: error")
                }
            }
        }
    }

    fun likeComment(position: Int, username: String) {
        val comment = parentComments.value!![position]
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
        private const val TAG = "PostViewModel"
    }
}