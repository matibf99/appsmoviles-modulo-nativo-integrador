package com.appsmoviles.gruposcomunitarios.presentation.userregister

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.usecases.RegisterUserUseCase
import com.appsmoviles.gruposcomunitarios.utils.helpers.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserRegisterStatus(
    val message: String? = null
) {
    object Success : UserRegisterStatus()
    class Error(message: String?) : UserRegisterStatus(message)
    object Loading : UserRegisterStatus()
}

@HiltViewModel
class UserRegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _statusRegistered: MutableLiveData<UserRegisterStatus> = MutableLiveData()
    val statusRegistered: LiveData<UserRegisterStatus> get() = _statusRegistered

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> get() = _email

    private val _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String> get() = _password

    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> get() = _name

    private val _surname: MutableLiveData<String> = MutableLiveData()
    val surname: LiveData<String> get() = _surname

    private val _username: MutableLiveData<String> = MutableLiveData()
    val username: LiveData<String> get() = _username

    private val _imageUri: MutableLiveData<Uri> = MutableLiveData()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _formUsernameStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formUsernameStatus: LiveData<FieldStatus> get() = _formUsernameStatus

    private val _formNameStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formNameStatus: LiveData<FieldStatus> get() = _formNameStatus

    private val _formSurnameStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formSurnameStatus: LiveData<FieldStatus> get() = _formSurnameStatus

    private val _formEmailStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formEmailStatus: LiveData<FieldStatus> get() = _formEmailStatus

    private val _formPasswordStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formPasswordStatus: LiveData<FieldStatus> get() = _formPasswordStatus

    private val _formImageStatus: MutableLiveData<FieldStatus> = MutableLiveData()
    val formImageStatus: LiveData<FieldStatus> get() = _formImageStatus

    fun setName(name: String) {
        _name.value = name
        validateName()
    }

    fun setSurname(surname: String) {
        _surname.value = surname
        validateSurname()
    }

    fun setUsername(username: String) {
        _username.value = username
        validateUsername()
    }

    fun setEmail(email: String) {
        _email.value = email
        validateEmail()
    }

    fun setPassword(password: String) {
        _password.value = password
        validatePassword()
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        validateImageUri()
    }

    fun isFormValid(): Boolean {
        validateUsername()
        validateName()
        validateSurname()
        validateEmail()
        validatePassword()
        validateImageUri()

        return formUsernameStatus.value == FieldStatus.VALID &&
                formNameStatus.value == FieldStatus.VALID &&
                formSurnameStatus.value == FieldStatus.VALID &&
                formEmailStatus.value == FieldStatus.VALID &&
                formPasswordStatus.value == FieldStatus.VALID &&
                formImageStatus.value == FieldStatus.VALID
    }

    private fun validateUsername() {
        if (username.value?.isEmpty() == true || username.value == null)
            _formUsernameStatus.value = FieldStatus.EMPTY
        else
            _formUsernameStatus.value = FieldStatus.VALID
    }

    private fun validateName() {
        if (name.value?.isEmpty() == true || name.value == null)
            _formNameStatus.value = FieldStatus.EMPTY
        else
            _formNameStatus.value = FieldStatus.VALID
    }

    private fun validateSurname() {
        if (surname.value?.isEmpty() == true || surname.value == null)
            _formSurnameStatus.value = FieldStatus.EMPTY
        else
            _formSurnameStatus.value = FieldStatus.VALID
    }

    private fun validateEmail() {
        if (email.value?.isEmpty() == true || email.value == null)
            _formEmailStatus.value = FieldStatus.EMPTY
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches())
            _formEmailStatus.value = FieldStatus.INVALID_EMAIL
        else
            _formEmailStatus.value = FieldStatus.VALID
    }

    private fun validatePassword() {
        if (password.value?.isEmpty() == true || password.value == null)
            _formPasswordStatus.value = FieldStatus.EMPTY
        else if (password.value!!.length < 6)
            _formPasswordStatus.value = FieldStatus.INVALID_PASSWORD
        else
            _formPasswordStatus.value = FieldStatus.VALID
    }

    private fun validateImageUri() {
        if (imageUri.value == null)
            _formImageStatus.value = FieldStatus.EMPTY
        else
            _formImageStatus.value = FieldStatus.VALID
    }


    fun registerUser(bitmap: Bitmap? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUserUseCase.registerUser(
                username = username.value!!,
                name = name.value!!,
                surname = surname.value!!,
                email = email.value!!,
                password = password.value!!,
                bitmap = bitmap
            ).collect {
                when (it) {
                    is Res.Success -> _statusRegistered.postValue(UserRegisterStatus.Success)
                    is Res.Loading -> _statusRegistered.postValue(UserRegisterStatus.Loading)
                    is Res.Error -> _statusRegistered.postValue(UserRegisterStatus.Error(it.message))
                }
            }
        }
    }
}