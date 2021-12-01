package com.appsmoviles.gruposcomunitarios.domain.usecases

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface GetGroupsByTagUseCase {
    fun getGroupsByTag(tag: String): Flow<Res<List<Group>>>
}

class GetGroupsByTagUseCaseImp(
    private val groupRepository: GroupRepository
) : GetGroupsByTagUseCase {
    override fun getGroupsByTag(tag: String):  Flow<Res<List<Group>>> = flow {
        emit(Res.Loading())
        val res = groupRepository.getGroupsByTag(tag).first()
        emit(res)
    }
}