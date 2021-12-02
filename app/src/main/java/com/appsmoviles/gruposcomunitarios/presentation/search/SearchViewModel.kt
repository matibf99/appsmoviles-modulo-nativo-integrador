package com.appsmoviles.gruposcomunitarios.presentation.search

import android.util.Log
import androidx.lifecycle.*
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupsByTagUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupsUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.SubscribeToGroupUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.UnsubscribeToGroupUseCase
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchGroupsStatus(
    val message: String? = null
) {
    object Success : SearchGroupsStatus()
    object Loading : SearchGroupsStatus()
    class Error(message: String?) : SearchGroupsStatus(message)
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val subscribeToGroupUseCase: SubscribeToGroupUseCase,
    private val unsubscribeToGroupUseCase: UnsubscribeToGroupUseCase,
    private val getGroupsByTagUseCase: GetGroupsByTagUseCase
) : ViewModel() {

    private var _status: MutableLiveData<SearchGroupsStatus> = MutableLiveData()
    val status: LiveData<SearchGroupsStatus> get() = _status

    private var _groups: MutableLiveData<List<Group>> = MutableLiveData()
    val groups: LiveData<List<Group>> get() = _groups

    private var _sortBy: MutableLiveData<SortBy> = MutableLiveData(SortBy.NAME_ASCENDING)
    val sortBy: LiveData<SortBy> get() = _sortBy

    init {
        loadGroups()
    }

    fun loadGroups(sortBy: SortBy = SortBy.NAME_DESCENDING) {
        viewModelScope.launch {
            getGroupsUseCase.getGroups(sortBy).collect {
                when(it) {
                    is Res.Loading -> {
                        _status.postValue(SearchGroupsStatus.Loading)
                    }
                    is Res.Success -> {
                        _groups.postValue(it.data!!)
                        _status.postValue(SearchGroupsStatus.Success)
                        Log.d(TAG, "groups: ${it.data}")
                    }
                    is Res.Error -> {
                        _status.postValue(SearchGroupsStatus.Error(it.message))
                    }
                }
            }
        }
    }

    fun searchGroups(tag: String) {
        viewModelScope.launch {
            getGroupsByTagUseCase.getGroupsByTag(tag).collect {
                when(it) {
                    is Res.Loading -> {
                        _status.postValue(SearchGroupsStatus.Loading)
                    }
                    is Res.Success -> {
                        _groups.postValue(it.data!!)
                        _status.postValue(SearchGroupsStatus.Success)
                        Log.d(TAG, "groups: ${it.data}")
                    }
                    is Res.Error -> {
                        _status.postValue(SearchGroupsStatus.Error(it.message))
                    }
                }
            }
        }
    }

    fun subscribeToGroup(position: Int) {
        val group = groups.value!![position]

        val isSubscribed = group.subscribed
        group.subscribed = !isSubscribed!!
        _groups.value = _groups.value

        viewModelScope.launch {
            val res = if (isSubscribed == true) unsubscribeToGroupUseCase.unsubscribeToGroup(group.documentId!!)
                else subscribeToGroupUseCase.subscribeToGroup(group.documentId!!)

            res.collect {
                when(it) {
                    is Res.Success-> Log.d(TAG, "subscribeToGroup: success...")
                    is Res.Error -> Log.d(TAG, "subscribeToGroup: error...")
                    is Res.Loading -> Log.d(TAG, "subscribeToGroup: loading...")
                }
            }
        }

    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
        loadGroups(sortBy)
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}