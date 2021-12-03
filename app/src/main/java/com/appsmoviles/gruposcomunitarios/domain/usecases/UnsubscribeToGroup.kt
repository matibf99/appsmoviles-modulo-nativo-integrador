package com.appsmoviles.gruposcomunitarios.domain.usecases

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface UnsubscribeToGroupUseCase {
    fun unsubscribeToGroup(groupId: String): Flow<Res<Nothing>>
}

class UnsubscribeToGroupUseCaseImp(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : UnsubscribeToGroupUseCase {
    override fun unsubscribeToGroup(groupId: String): Flow<Res<Nothing>> = flow {
        emit(Res.Loading())
        val userId = userRepository.getCurrentUserDocumentId().first()
        val result = groupRepository.unsubscribeToGroup(groupId, userId.data!!).first()
        emit(result)
    }
}