package com.appsmoviles.gruposcomunitarios.presentation.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor() : ViewModel() {

    private val _group: MutableLiveData<Group> = MutableLiveData()
    val group: LiveData<Group> get() = _group

    fun setGroup(group: Group) {
        _group.value = group
    }
}