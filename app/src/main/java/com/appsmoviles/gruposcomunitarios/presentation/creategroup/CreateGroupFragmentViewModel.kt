package com.appsmoviles.gruposcomunitarios.presentation.creategroup

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.usecases.CreateGroupUseCase
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateGroupStatus(
    val message: String? = null
) {
    object Success : CreateGroupStatus()
    class Error(message: String?) : CreateGroupStatus(message)
    object Loading : CreateGroupStatus()
}

@HiltViewModel
class CreateGroupFragmentViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {

    private val _status: MutableLiveData<CreateGroupStatus> = MutableLiveData()
    val status: LiveData<CreateGroupStatus> get() = _status

    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> get() = _name

    private val _description: MutableLiveData<String> = MutableLiveData()
    val description: LiveData<String> get() = _description

    private val _tags: MutableLiveData<String> = MutableLiveData()
    val tags: LiveData<String> get() = _tags

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri> get() = _imageUri

    fun setName(name: String) {
        _name.value = name
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setTags(tags: String) {
        _tags.value = tags
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun createGroup(bitmap: Bitmap) {
        viewModelScope.launch {
            createGroupUseCase.createGroup(name.value!!, description.value!!, tags.value!!, bitmap).collect {
                when (it) {
                    is Res.Success -> _status.postValue(CreateGroupStatus.Success)
                    is Res.Loading -> _status.postValue(CreateGroupStatus.Loading)
                    is Res.Error -> _status.postValue(CreateGroupStatus.Error(it.message))
                }
            }
        }
    }
}