package com.appsmoviles.gruposcomunitarios.presentation.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetPostsFromGroupUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.LikePostUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnlikePostUseCase
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val unlikePostUseCase: UnlikePostUseCase
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
    }

    fun setGroupId(groupId: String) {
        Log.d(TAG, "setGroupId: set groupId")
        Log.d(TAG, "setGroupId: ${group.value?.documentId} = $groupId")
        viewModelScope.launch {
            Log.d(TAG, "setGroupId: set groupId2")

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
        viewModelScope.launch {
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
        private const val TAG = "GroupViewModel"
    }
}