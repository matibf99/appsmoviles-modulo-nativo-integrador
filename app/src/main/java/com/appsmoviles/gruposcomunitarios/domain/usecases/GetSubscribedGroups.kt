package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface  GetSubscribedGroupsUseCase {
    fun getSubscribedGroups(): Flow<Res<List<Group>>>
}

class GetSubscribedGroupsUseCaseImp(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : GetSubscribedGroupsUseCase {
    override fun getSubscribedGroups(): Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())

        val userInfo = userRepository.getCurrentUserInfo().first()
        if (userInfo !is Res.Success)
            return@flow

        val subscribedGroups = userInfo.data?.groups
        val res = groupRepository.getSubscribedGroups(subscribedGroups!!).first()

        if (res !is Res.Success) {
            emit(res)
            return@flow
        }

        val groups = res.data!!
        for (group in groups) {
            group.userRol = when {
                userInfo.data.username == group.createdBy -> "Owner"
                group.moderators!!.contains(userInfo.data.username!!) -> "Moderator"
                else -> "User"
            }
        }

        emit(Res.Success(groups))
    }
}