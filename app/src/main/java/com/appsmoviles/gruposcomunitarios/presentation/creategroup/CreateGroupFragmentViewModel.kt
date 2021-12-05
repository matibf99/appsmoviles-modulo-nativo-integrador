package com.appsmoviles.gruposcomunitarios.presentation.creategroup

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.usecases.CreateGroupUseCase
import com.appsmoviles.gruposcomunitarios.utils.helpers.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _formNameStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formNameStatus: LiveData<FieldStatus> get() = _formNameStatus

    private val _formDescriptionStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formDescriptionStatus: LiveData<FieldStatus> get() = _formDescriptionStatus

    private val _formTagsStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formTagsStatus: LiveData<FieldStatus> get() = _formTagsStatus

    private val _formImageStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formImageStatus: LiveData<FieldStatus> get() = _formImageStatus

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri> get() = _imageUri

    fun setName(name: String) {
        _name.value = name
        validateName()
    }

    fun setDescription(description: String) {
        _description.value = description
        validateDescription()
    }

    fun setTags(tags: String) {
        _tags.value = tags
        validateTags()
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateImage()
    }

    fun isFormValid(): Boolean {
        validateName()
        validateDescription()
        validateTags()
        validateImage()

        return formNameStatus.value == FieldStatus.VALID &&
                formDescriptionStatus.value == FieldStatus.VALID &&
                formImageStatus.value == FieldStatus.VALID
    }

    private fun validateName() {
        if (name.value?.isEmpty() == true || name.value == null)
            _formNameStatus.value = FieldStatus.EMPTY
        else
            _formNameStatus.value = FieldStatus.VALID
    }

    private fun validateDescription() {
        if (description.value?.isEmpty() == true || description.value == null)
            _formDescriptionStatus.value = FieldStatus.EMPTY
        else
            _formDescriptionStatus.value = FieldStatus.VALID
    }

    private fun validateTags() {
        if (tags.value?.isEmpty() == true || tags.value == null)
            _formTagsStatus.value = FieldStatus.EMPTY
        else
            _formTagsStatus.value = FieldStatus.VALID
    }

    private fun validateImage() {
        if (imageUri.value == null)
            _formImageStatus.value = FieldStatus.EMPTY
        else
            _formImageStatus.value = FieldStatus.VALID
    }

    fun createGroup(username: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            createGroupUseCase.createGroup(
                name.value!!,
                description.value!!,
                tags.value ?: "",
                username,
                bitmap
            ).collect {
                when (it) {
                    is Res.Success -> _status.postValue(CreateGroupStatus.Success)
                    is Res.Loading -> _status.postValue(CreateGroupStatus.Loading)
                    is Res.Error -> _status.postValue(CreateGroupStatus.Error(it.message))
                }
            }
        }
    }
}