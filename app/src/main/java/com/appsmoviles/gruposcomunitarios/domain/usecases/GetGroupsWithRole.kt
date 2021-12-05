package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetGroupsWithRoleUseCase {
    fun getGroups(username: String) : Flow<Res<List<Group>>>
}

class GetGroupsWithRoleUseCaseImp(
    private val groupRepository: GroupRepository,
) : GetGroupsWithRoleUseCase {
    override fun getGroups(username: String): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())
        val groupsWithRole = groupRepository.getGroupsWithRole(username).first()
        emit(groupsWithRole)
    }
}