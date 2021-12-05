package com.appsmoviles.gruposcomunitarios.domain.repository

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.helpers.SortBy
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroups(sortBy: SortBy): Flow<Res<List<Group>>>
    suspend fun getGroupsByTag(tag: String): Flow<Res<List<Group>>>
    suspend fun getGroupInfo(groupId: String): Flow<Res<Group>>
    suspend fun createGroup(group: Group): Flow<Res<Nothing>>
    suspend fun getSubscribedGroups(username: String): Flow<Res<List<Group>>>
    suspend fun getGroupsWithRole(username: String): Flow<Res<List<Group>>>
    suspend fun subscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>>
    suspend fun unsubscribeToGroup(groupId: String, username: String): Flow<Res<Nothing>>
}