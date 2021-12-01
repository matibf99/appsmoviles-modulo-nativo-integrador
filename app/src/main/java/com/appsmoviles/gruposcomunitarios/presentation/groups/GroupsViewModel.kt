package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupsWithRoleUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetSubscribedGroupsUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnsubscribeToGroupUseCase
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GroupsStatus {
    LOADING, LOADED, FAILED
}

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val getSubscribedGroupsUseCase: GetSubscribedGroupsUseCase,
    private val getGroupsWithRoleUseCase: GetGroupsWithRoleUseCase,
    private val unsubscribeToGroupUseCase: UnsubscribeToGroupUseCase,
) : ViewModel() {

    private val _status: MutableLiveData<GroupsStatus> = MutableLiveData(GroupsStatus.LOADING)
    val status: LiveData<GroupsStatus> get() = _status

    private val _subscribedGroups: MutableLiveData<List<Group>> = MutableLiveData(ArrayList())
    val subscribedGroups: LiveData<List<Group>> get() = _subscribedGroups

    private val _groupsWithRole: MutableLiveData<List<Group>> = MutableLiveData(ArrayList())
    val groupsWithRole: LiveData<List<Group>> get() = _groupsWithRole

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            getSubscribedGroupsUseCase.getSubscribedGroups().collect {
                when(it) {
                    is Res.Success -> {
                        _status.postValue(GroupsStatus.LOADED)
                        _subscribedGroups.postValue(it.data!!)
                    }
                    is Res.Error -> {
                        _status.postValue(GroupsStatus.FAILED)
                    }
                    is Res.Loading -> {
                        _status.postValue(GroupsStatus.LOADING)
                    }
                }
            }
        }

        viewModelScope.launch {
            getGroupsWithRoleUseCase.getGroupsWithUseCase().collect {
                when(it) {
                    is Res.Success -> {
                        _status.postValue(GroupsStatus.LOADED)
                        _groupsWithRole.postValue(it.data!!)
                    }
                    is Res.Error -> {
                        _status.postValue(GroupsStatus.FAILED)
                    }
                    is Res.Loading -> {
                        _status.postValue(GroupsStatus.LOADING)
                    }
                }
            }
        }
    }

    fun unsubscribeTo(position: Int) {
        viewModelScope.launch {
            val group = subscribedGroups.value?.get(position) ?: return@launch

            val groups = _subscribedGroups.value!!.toMutableList()
            groups.removeAt(position)
            _subscribedGroups.postValue(groups)

            unsubscribeToGroupUseCase.unsubscribeToGroup(group.documentId!!).collect {
                when (it) {
                    is Res.Success -> Log.d(TAG, "unsubscribeTo: unsubscribed successfully")
                    is Res.Loading -> Log.d(TAG, "unsubscribeTo: unsubscribing...")
                    is Res.Error -> Log.d(TAG, "unsubscribeTo: error in unsubscribe: ${it.message}")
                }
            }

        }
    }

    companion object {
        private const val TAG = "GroupsViewModel"
    }

}