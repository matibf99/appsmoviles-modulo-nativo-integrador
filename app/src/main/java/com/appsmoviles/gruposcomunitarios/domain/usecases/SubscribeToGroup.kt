package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface SubscribeToGroupUseCase {
    fun subscribeToGroup(groupId: String): Flow<Res<Nothing>>
}

class SubscribeToGroupUseCaseImp(
    private val userRepository: UserRepository
) : SubscribeToGroupUseCase {
    override fun subscribeToGroup(groupId: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val result = userRepository.subscribeToGroup(groupId).first()
        emit(result)
    }
}