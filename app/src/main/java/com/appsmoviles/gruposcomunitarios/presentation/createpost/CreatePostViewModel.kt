package com.appsmoviles.gruposcomunitarios.presentation.createpost

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.usecases.CreatePostUseCase
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreatePostStatus(
    val message: String? = null
) {
    object Successful : CreatePostStatus()
    class Error(message: String?) : CreatePostStatus(message)
    object Loading : CreatePostStatus()
}

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _group: MutableLiveData<Group> = MutableLiveData()
    val group: LiveData<Group> get() = _group

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> get() = _title

    private val _content: MutableLiveData<String> = MutableLiveData()
    val content: LiveData<String> get() = _content

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _createPostStatus: MutableLiveData<CreatePostStatus> = MutableLiveData()
    val createPostStatus: LiveData<CreatePostStatus> get() = _createPostStatus

    fun setGroup(group: Group) {
        _group.value = group
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun createPost(bitmap: Bitmap? = null) {
        viewModelScope.launch {
            createPostUseCase.createPost(
                groupId = group.value?.documentId!!,
                title = title.value!!,
                content = content.value!!,
                imageBitmap = bitmap
            ).collect {
                Log.d(TAG, "createPost: ${it.toString()}")
                when(it) {
                    is Res.Success -> _createPostStatus.postValue(CreatePostStatus.Successful)
                    is Res.Error -> _createPostStatus.postValue(CreatePostStatus.Error(it.message))
                    is Res.Loading -> _createPostStatus.postValue(CreatePostStatus.Loading)
                }
            }
        }
    }

    companion object {
        private const val TAG = "CreatePostViewModel"
    }
}