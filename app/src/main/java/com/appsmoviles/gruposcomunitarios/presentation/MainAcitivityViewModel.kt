package com.appsmoviles.gruposcomunitarios.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetUserInfoUseCase
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UserStatus {
    LOADING, SUCCESS, ERROR
}

@HiltViewModel
class MainAcitivityViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _userStatus: MutableLiveData<UserStatus> = MutableLiveData()
    val userStatus: LiveData<UserStatus> get() = _userStatus

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            getUserInfoUseCase.getUserInfo().collect {
                when (it) {
                    is Res.Success -> {
                        _user.postValue(it.data!!)
                        _userStatus.postValue(UserStatus.SUCCESS)
                        Log.d(TAG, "loadUser: success loading user")
                    }
                    is Res.Error -> {
                        _userStatus.postValue(UserStatus.ERROR)
                        Log.d(TAG, "loadUser: error loading user: ${it.message}")
                    }
                    is Res.Loading -> {
                        _userStatus.postValue(UserStatus.LOADING)
                        Log.d(TAG, "loadUser: loading user")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}