package com.appsmoviles.gruposcomunitarios.presentation.userlogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.usecases.LogInUserUseCase
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserLogInStatus(
    val message: String? = null
) {
    object Success : UserLogInStatus()
    class Error(message: String?) : UserLogInStatus(message)
    object Loading : UserLogInStatus()
}

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val logInUserUseCase: LogInUserUseCase
) : ViewModel() {

    private val _logInStatus: MutableLiveData<UserLogInStatus> = MutableLiveData()
    val logInStatus: LiveData<UserLogInStatus> get() = _logInStatus

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> get() = _email

    private val _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String> get() = _password

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun logInUser() {
        viewModelScope.launch(Dispatchers.IO) {
            logInUserUseCase.logIn(email = email.value!!, password = password.value!!).collect {
                when(it) {
                    is Res.Success -> _logInStatus.postValue(UserLogInStatus.Success)
                    is Res.Loading -> _logInStatus.postValue(UserLogInStatus.Loading)
                    is Res.Error -> _logInStatus.postValue(UserLogInStatus.Error(it.message))
                }
            }
        }
    }
}