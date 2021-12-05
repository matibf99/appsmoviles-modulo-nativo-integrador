package com.appsmoviles.gruposcomunitarios.presentation.createpost

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.usecases.CreatePostUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetLocationUseCase
import com.appsmoviles.gruposcomunitarios.utils.helpers.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val createPostUseCase: CreatePostUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    private val _group: MutableLiveData<Group> = MutableLiveData()
    val group: LiveData<Group> get() = _group

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> get() = _title

    private val _content: MutableLiveData<String> = MutableLiveData()
    val content: LiveData<String> get() = _content

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _location: MutableLiveData<Location?> = MutableLiveData()
    val location: LiveData<Location?> get() = _location

    private val _fieldTitleStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val fieldTitleStatus: LiveData<FieldStatus> get() = _fieldTitleStatus

    private val _createPostStatus: MutableLiveData<CreatePostStatus> = MutableLiveData()
    val createPostStatus: LiveData<CreatePostStatus> get() = _createPostStatus

    fun setGroup(group: Group) {
        _group.value = group
    }

    fun setTitle(title: String) {
        _title.value = title

        if (title.isEmpty())
            _fieldTitleStatus.value = FieldStatus.EMPTY
        else
            _fieldTitleStatus.value = FieldStatus.VALID
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun removeLocation() {
        _location.value = null
    }

    fun isFormValid(): Boolean {
        validateTitle()
        return _fieldTitleStatus.value == FieldStatus.VALID
    }

    private fun validateTitle() {
        if (_title.value?.isEmpty() == true || _title.value == null)
            _fieldTitleStatus.value = FieldStatus.EMPTY
        else
            _fieldTitleStatus.value = FieldStatus.VALID
    }

    fun getLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getLocationUseCase.getLocation().collect {
                when(it) {
                    is Res.Success -> {
                        _location.postValue(it.data!!)
                        Log.d(TAG, "getLocation: location: ${it.data}")
                    }
                    is Res.Loading -> Log.d(TAG, "getLocation: loading location")
                    is Res.Error -> Log.d(TAG, "getLocation: error loading location: ${it.message}")
                }
            }
        }
    }

    fun createPost(username: String, bitmap: Bitmap? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            createPostUseCase.createPost(
                groupId = group.value?.documentId!!,
                groupName = group.value?.name!!,
                title = title.value!!,
                content = content.value ?: "",
                username = username,
                imageBitmap = bitmap,
                latitude = location.value?.latitude,
                longitude = location.value?.longitude,
            ).collect {
                Log.d(TAG, "createPost: $it")
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