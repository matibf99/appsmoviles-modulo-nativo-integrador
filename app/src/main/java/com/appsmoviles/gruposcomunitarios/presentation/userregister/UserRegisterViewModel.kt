package com.appsmoviles.gruposcomunitarios.presentation.userregister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.usecases.RegisterUserUseCase
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

    fun setName(name: String) {
        _name.value = name
    }

    fun setSurname(surname: String) {
        _surname.value = surname
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun registerUser() {
        viewModelScope.launch(Dispatchers.IO) {
            registerUserUseCase.registerUser(
                username = username.value!!,
                name = name.value!!,
                surname = surname.value!!,
                email = email.value!!,
                password = password.value!!
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