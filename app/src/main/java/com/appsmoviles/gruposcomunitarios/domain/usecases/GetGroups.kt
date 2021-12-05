package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetGroupsUseCase {
    suspend fun getGroups(sortBy: SortBy): Flow<Res<List<Group>>>
}

class GetGroupsUseCaseImp(
    private val groupRepository: GroupRepository,
) : GetGroupsUseCase {
    override suspend fun getGroups(sortBy: SortBy): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())
        val groupsRes = groupRepository.getGroups(sortBy).first()
        emit(groupsRes)
    }
}