package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetGroupsWithRoleUseCase {
    fun getGroupsWithUseCase() : Flow<Res<List<Group>>>
}

class GetGroupsWithRoleUseCaseImp(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : GetGroupsWithRoleUseCase {
    override fun getGroupsWithUseCase(): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())
        val userInfo = userRepository.getCurrentUserInfo().first()

        if (userInfo !is Res.Success) {
            emit(Res.Error(userInfo.message))
            return@flow
        }

        val username = userInfo.data!!.username!!
        val groupsWithRole = groupRepository.getGroupsWithRole(username).first()
        emit(groupsWithRole)
    }
}