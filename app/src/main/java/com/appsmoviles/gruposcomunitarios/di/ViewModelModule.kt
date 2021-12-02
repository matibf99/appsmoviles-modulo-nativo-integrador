package com.appsmoviles.gruposcomunitarios.di

import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    fun provideGetGroupsUseCase(groupRepository: GroupRepository, userRepository: UserRepository): GetGroupsUseCase =
        GetGroupsUseCaseImp(groupRepository, userRepository)

    @Provides
    fun provideSubscribeToGroupUseCase(userRepository: UserRepository): SubscribeToGroupUseCase =
        SubscribeToGroupUseCaseImp(userRepository)

    @Provides
    fun provideUnsubscribeToGroupUseCase(userRepository: UserRepository): UnsubscribeToGroupUseCase =
        UnsubscribeToGroupUseCaseImp(userRepository)

    @Provides
    fun provideGetGroupsByTagUseCase(groupRepository: GroupRepository): GetGroupsByTagUseCase =
        GetGroupsByTagUseCaseImp(groupRepository)

    @Provides
    fun provideGetSubscribedGroups(groupRepository: GroupRepository, userRepository: UserRepository): GetSubscribedGroupsUseCase =
        GetSubscribedGroupsUseCaseImp(groupRepository, userRepository)

    @Provides
    fun provideGetUserInfo(userRepository: UserRepository): GetUserInfoUseCase =
        GetUserInfoUseCaseImp(userRepository)

    @Provides
    fun provideGetGroupsWithRole(groupRepository: GroupRepository, userRepository: UserRepository): GetGroupsWithRoleUseCase =
        GetGroupsWithRoleUseCaseImp(groupRepository, userRepository)

    @Provides
    fun provideCreateGroupUseCase(groupRepository: GroupRepository, userRepository: UserRepository, storageRepository: StorageRepository): CreateGroupUseCase =
        CreateGroupUseCaseImp(groupRepository, userRepository, storageRepository)

    @Provides
    fun provideCreatePostUseCase(postRepository: PostRepository, userRepository: UserRepository, storageRepository: StorageRepository): CreatePostUseCase =
        CreatePostUseCaseImp(postRepository, userRepository, storageRepository)
}