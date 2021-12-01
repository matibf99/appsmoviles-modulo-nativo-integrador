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
    private val userRepository: UserRepository
) : GetGroupsUseCase {
    override suspend fun getGroups(sortBy: SortBy): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())

        val userInfo = userRepository.getCurrentUserInfo().first()

        val groupsRes = groupRepository.getGroups(sortBy).first()
        val subscribedGroupsRes = userInfo.data?.let { groupRepository.getSubscribedGroups(it.groups!!).first() }

        if (groupsRes !is Res.Success || userInfo !is Res.Success || subscribedGroupsRes !is Res.Success) {
            emit(Res.Error())
            return@flow
        }

        val groups = groupsRes.data
        val subscribedGroups = subscribedGroupsRes.data

        if (subscribedGroups != null && groups != null) {
            for (subs in subscribedGroups) {
                groups.find { it.documentId == subs.documentId }?.subscribed = true
            }
        }

        emit(Res.Success(groups))
    }
}