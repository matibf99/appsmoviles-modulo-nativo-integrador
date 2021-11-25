package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroups(sortBy: Int): Flow<Res<List<Group>>>
    suspend fun getGroupsByTag(tag: String): Flow<Res<List<Group>>>
    suspend fun getGroupInfo(groupId: String): Flow<Res<Group>>
    suspend fun createGroup(group: Group): Flow<Res<Group>>
    suspend fun getSubscribedGroups(): Flow<Res<List<Group>>>
    suspend fun subscribeToGroup(groupId: String): Flow<Res<Nothing>>
    suspend fun unsubscribeToGroup(groupId: String): Flow<Res<Nothing>>
}