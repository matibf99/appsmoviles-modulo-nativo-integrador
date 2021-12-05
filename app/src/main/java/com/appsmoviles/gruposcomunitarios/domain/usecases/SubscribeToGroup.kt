package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface SubscribeToGroupUseCase {
    fun subscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>>
}

class SubscribeToGroupUseCaseImp(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : SubscribeToGroupUseCase {
    override fun subscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val result = groupRepository.subscribeToGroup(groupId, username).first()
        emit(result)
    }
}