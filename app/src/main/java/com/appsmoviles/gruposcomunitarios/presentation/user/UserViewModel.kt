package com.appsmoviles.gruposcomunitarios.presentation.user

import android.util.Log
import androidx.lifecycle.*
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetUserInfoUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetUserRegisteredUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.RegisterUserCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.SingInUserCase
import com.appsmoviles.gruposcomunitarios.presentation.search.SearchGroupsStatus
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UserStatus {
    LOADING, LOADED, FAILED
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserRegistered: GetUserRegisteredUseCase,
    private val singInUserCase: SingInUserCase,
    private val registerUserCase: RegisterUserCase
) : ViewModel() {

    private var _status: MutableLiveData<UserStatus> = MutableLiveData(UserStatus.LOADING)
    val status: LiveData<UserStatus> get() = _status

    private var _statusRegistered: MutableLiveData<Boolean> = MutableLiveData()
    val statusRegistered: LiveData<Boolean> get() = _statusRegistered

    private var _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    private var _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> get() = _email

    private var _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String> get() = _password

    private var _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> get() = _name

    private var _surname: MutableLiveData<String> = MutableLiveData()
    val surname: LiveData<String> get() = _surname

    private var _username: MutableLiveData<String> = MutableLiveData()
    val username: LiveData<String> get() = _username




    private fun loadUsers() {

        viewModelScope.launch {
            getUserInfoUseCase.getUserInfo().collect {
                when (it) {
                    is Res.Loading -> {
                        _status.postValue(UserStatus.LOADING)
                        _user.postValue(it.data!!)
                    }
                    is Res.Success -> {
                        _status.postValue(UserStatus.LOADED)
                    }
                    is Res.Error -> {
                        _status.postValue(UserStatus.FAILED)
                    }
                }
            }
        }

        //Status change view
        val registered = getUserRegistered.registeredUser()
        _statusRegistered.value = registered

    }

    fun setName (name:String) {
        _name.value = name
    }
    fun setSurname (surname:String) {
        _surname.value = surname
    }
    fun setUsername (username:String) {
        _username.value = username
    }
    fun setEmail (email:String) {
        _email.value = email
    }
    fun setPassword (password:String) {
        _password.value = password
    }

    fun signIn() {
        viewModelScope.launch {
            singInUserCase.signIn(email.value!!, password.value!!).collect {
                when (it) {
                    is Res.Loading -> {
                        _status.postValue(UserStatus.LOADING)


                    }
                    is Res.Success -> {
                        _user.postValue(it.data!!)
                        _status.postValue(UserStatus.LOADED)

                    }
                    is Res.Error -> {
                        _status.postValue(UserStatus.FAILED)
                    }
                }
            }
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            registerUserCase.registerUser(user.value!!, password.value!!).collect {
                when (it) {
                    is Res.Loading -> {
                        _status.postValue(UserStatus.LOADING)
                        _user.postValue(it.data!!)

                    }
                    is Res.Success -> {
                        _status.postValue(UserStatus.LOADED)
                    }
                    is Res.Error -> {
                        _status.postValue(UserStatus.FAILED)
                    }
                }
            }
        }
    }
}






