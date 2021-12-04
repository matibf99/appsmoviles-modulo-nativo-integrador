package com.appsmoviles.gruposcomunitarios.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetPostsFromAllGroupsUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.LikePostUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnlikePostUseCase
import com.appsmoviles.gruposcomunitarios.presentation.group.GroupViewModel
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomePostsStatus(
    val message: String? = null,
) {
    object Success : HomePostsStatus()
    object Loading : HomePostsStatus()
    class Error(message: String?) : HomePostsStatus(message)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostsFromAllGroupsUseCase: GetPostsFromAllGroupsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase
) : ViewModel() {

    private val _status: MutableLiveData<HomePostsStatus> = MutableLiveData()
    val status: LiveData<HomePostsStatus> get() = _status

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData(ArrayList())
    val posts: LiveData<List<Post>> get() = _posts

    init {
        Log.d(TAG, "init: viewmodel created")
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            _status.postValue(HomePostsStatus.Loading)

            getPostsFromAllGroupsUseCase.getPosts(SortBy.CREATED_AT_DESCENDING).collect {
                when(it) {
                    is Res.Success -> {
                        _status.postValue(HomePostsStatus.Success)
                        _posts.postValue(it.data!!)
                    }
                    is Res.Loading -> _status.postValue(HomePostsStatus.Loading)
                    is Res.Error -> _status.postValue(HomePostsStatus.Error(it.message))
                }
            }
        }
    }

    fun likePost(position: Int, username: String) {
        val post = posts.value!![position]
        Log.d(TAG, "likePost: $post")
        val likePost: Boolean

        if (post.likes?.contains(username) == true) {
            post.likes = post.likes?.filter { it != username }
            likePost = false
        }
        else {
            val likes = post.likes!!.toMutableList()
            likes.add(username)
            post.likes = likes
            likePost = true
        }

        viewModelScope.launch {
            val res = if (likePost) likePostUseCase.likePost(post.groupId!!, post.documentId!!, username)
            else unlikePostUseCase.unlikePost(post.groupId!!, post.documentId!!, username)

            res.collect {
                when(it) {
                    is Res.Success -> Log.d(TAG, "likePost: success")
                    is Res.Loading -> Log.d(TAG, "likePost: loading")
                    is Res.Error -> Log.d(TAG, "likePost: error")
                }
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}