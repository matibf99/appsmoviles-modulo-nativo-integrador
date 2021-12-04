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


    init {
        loadUsers()
    }

    private fun loadUsers() {

        viewModelScope.launch  {
            getUserInfoUseCase.getUserInfo().collect {
                when(it) {
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

        private fun signIn(email:String, password:String) {
            viewModelScope.launch {
                singInUserCase.signIn(email,password).collect {
                    when(it) {
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

        private fun registerUser(user: User, password:String) {
            viewModelScope.launch {
                registerUserCase.registerUser(user,password).collect {
                    when(it) {
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






