package com.appsmoviles.gruposcomunitarios.presentation.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.usecases.*
import com.appsmoviles.gruposcomunitarios.presentation.search.SearchViewModel
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GroupStatus (
    val message: String? = null
) {
    object Success : GroupStatus()
    object Loading : GroupStatus()
    class Error(message: String?) : GroupStatus(message)
}

sealed class GroupPostsStatus(
    val message: String? = null,
) {
    object Success : GroupPostsStatus()
    object Loading : GroupPostsStatus()
    class Error(message: String?) : GroupPostsStatus(message)
}

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val getGroupUseCase: GetGroupUseCase,
    private val getPostsFromGroupUseCase: GetPostsFromGroupUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val unsubscribeToGroupUseCase: UnsubscribeToGroupUseCase,
    private val subscribeToGroupUseCase: SubscribeToGroupUseCase
) : ViewModel() {

    private val _groupStatus: MutableLiveData<GroupStatus> = MutableLiveData()
    val groupStatus: LiveData<GroupStatus> get() = _groupStatus

    private val _status: MutableLiveData<GroupPostsStatus> = MutableLiveData()
    val status: LiveData<GroupPostsStatus> get() = _status

    private val _group: MutableLiveData<Group> = MutableLiveData()
    val group: LiveData<Group> get() = _group

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData(ArrayList())
    val posts: LiveData<List<Post>> get() = _posts

    fun setGroup(group: Group) {
        Log.d(TAG, "setGroup: set group")
        if (this.group.value?.documentId == group.documentId)
            return
        Log.d(TAG, "setGroup: set group2")

        _group.value = group
        _groupStatus.value = GroupStatus.Success
        getPosts()

        loadGroup(group.documentId!!)
    }

    fun setGroupId(groupId: String) {
        loadGroup(groupId)
    }

    private fun loadGroup(groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getGroupUseCase.getGroup(groupId).collect {
                when(it) {
                    is Res.Success -> {
                        _group.postValue(it.data!!)
                        _groupStatus.postValue(GroupStatus.Success)
                    }
                    is Res.Loading -> _groupStatus.postValue(GroupStatus.Loading)
                    is Res.Error -> _groupStatus.postValue(GroupStatus.Error(it.message))
                }
            }
        }
    }

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            getPostsFromGroupUseCase.getPosts(
                _group.value!!.documentId!!,
                SortBy.CREATED_AT_DESCENDING
            ).collect {
                when(it) {
                    is Res.Success -> {
                        _status.postValue(GroupPostsStatus.Success)
                        _posts.postValue(it.data!!)
                    }
                    is Res.Loading -> _status.postValue(GroupPostsStatus.Loading)
                    is Res.Error -> _status.postValue(GroupPostsStatus.Error(it.message))
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

        viewModelScope.launch(Dispatchers.IO) {
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

    fun subscribeToGroup(username: String) {
        val group = group.value!!

        val isSubscribed = group.subscribed?.contains(username) == true
        val subscribed = group.subscribed!!.toMutableList()

        if (isSubscribed)
            subscribed.remove(username)
        else
            subscribed.add(username)

        _group.value!!.subscribed = subscribed
        _group.value = group

        viewModelScope.launch(Dispatchers.IO) {
            val res = if (isSubscribed) unsubscribeToGroupUseCase.unsubscribeToGroup(group.documentId!!, username)
            else subscribeToGroupUseCase.subscribeToGroup(group.documentId!!, username)

            res.collect {
                when(it) {
                    is Res.Success-> Log.d(TAG, "subscribeToGroup: success...")
                    is Res.Error -> Log.d(TAG, "subscribeToGroup: error...")
                    is Res.Loading -> Log.d(TAG, "subscribeToGroup: loading...")
                }
            }
        }
    }
    
    companion object {
        private const val TAG = "GroupViewModel"
    }
}