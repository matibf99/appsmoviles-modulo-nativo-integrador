package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetGroupUseCase {
    fun getGroup(groupId: String): Flow<Res<Group>>
}

class GetGroupUseCaseImp(
    val groupRepository: GroupRepository
) : GetGroupUseCase {
    override fun getGroup(groupId: String): Flow<Res<Group>> = flow {
        emit(Res.Loading())
        val groupRes = groupRepository.getGroupInfo(groupId).first()
        emit(groupRes)
    }

}