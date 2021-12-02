package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGroupFragmentViewModel @Inject constructor() : ViewModel() {

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
}