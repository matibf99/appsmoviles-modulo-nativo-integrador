package com.appsmoviles.gruposcomunitarios.di

import com.appsmoviles.gruposcomunitarios.domain.repository.*
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
    fun provideGetGroupsUseCase(groupRepository: GroupRepository): GetGroupsUseCase =
        GetGroupsUseCaseImp(groupRepository)

    @Provides
    fun provideSubscribeToGroupUseCase(userRepository: UserRepository, groupRepository: GroupRepository): SubscribeToGroupUseCase =
        SubscribeToGroupUseCaseImp(userRepository, groupRepository)

    @Provides
    fun provideUnsubscribeToGroupUseCase(userRepository: UserRepository, groupRepository: GroupRepository): UnsubscribeToGroupUseCase =
        UnsubscribeToGroupUseCaseImp(userRepository, groupRepository)

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

    @Provides
    fun provideGetPostsUseCase(postRepository: PostRepository): GetPostsFromGroupUseCase =
        GetPostsFromGroupUseCaseImp(postRepository)

    @Provides
    fun provideLikePostUseCase(postRepository: PostRepository): LikePostUseCase =
        LikePostUseCaseImp(postRepository)

    @Provides
    fun provideUnlikePostUseCase(postRepository: PostRepository): UnlikePostUseCase =
        UnlikePostUseCaseImp(postRepository)

    @Provides
    fun provideGetPostsFromAllGroupsUseCase(postRepository: PostRepository): GetPostsFromAllGroupsUseCase =
        GetPostsFromAllGroupsUseCaseImp(postRepository)

    @Provides
    fun provideGetGroupUseCase(groupRepository: GroupRepository): GetGroupUseCase =
        GetGroupUseCaseImp(groupRepository)

    @Provides
    fun provideGetCommentsFromPostUseCase(postRepository: PostRepository): GetCommentsFromPostUseCase =
        GetCommentsFromPostUseCaseImp(postRepository)

    @Provides
    fun provideLikeCommentUseCase(postRepository: PostRepository): LikeCommentUseCase =
        LikeCommentUseCaseImp(postRepository)

    @Provides
    fun provideUnlikeCommentUseCase(postRepository: PostRepository): UnlikeCommentUseCase =
        UnlikeCommentUseCaseImp(postRepository)

    @Provides
    fun provideGetPostFromGroupUseCase(postRepository: PostRepository): GetPostFromGroupUseCase =
        GetPostFromGroupUseCaseImp(postRepository)

    @Provides
    fun provideCreateCommentUseCase(postRepository: PostRepository): CreateCommentUseCase =
        CreateCommentUseCaseImp(postRepository)

    @Provides
    fun provideGetLocationUseCase(locationRepository: LocationRepository): GetLocationUseCase =
        GetLocationUseCaseImp(locationRepository)
}