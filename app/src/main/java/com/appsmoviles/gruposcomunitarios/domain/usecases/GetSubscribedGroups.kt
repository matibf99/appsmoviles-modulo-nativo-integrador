package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface  GetSubscribedGroupsUseCase {
    fun getSubscribedGroups(username: String): Flow<Res<List<Group>>>
}

class GetSubscribedGroupsUseCaseImp(
    private val groupRepository: GroupRepository
) : GetSubscribedGroupsUseCase {
    override fun getSubscribedGroups(username: String): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())
        val res = groupRepository.getSubscribedGroups(username).first()
        emit(res)
    }
}