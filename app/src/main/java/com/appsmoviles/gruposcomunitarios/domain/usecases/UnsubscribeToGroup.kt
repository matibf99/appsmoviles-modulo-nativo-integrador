package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface UnsubscribeToGroupUseCase {
    fun unsubscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>>
}

class UnsubscribeToGroupUseCaseImp(
    private val groupRepository: GroupRepository
) : UnsubscribeToGroupUseCase {
    override fun unsubscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val result = groupRepository.unsubscribeToGroup(groupId, username).first()
        emit(result)
    }
}