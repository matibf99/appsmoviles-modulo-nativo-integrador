package com.appsmoviles.gruposcomunitarios.di

import com.appsmoviles.gruposcomunitarios.domain.repository.*
import com.appsmoviles.gruposcomunitarios.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {
    @Provides
    fun provideGetGroupsUseCase(groupRepository: GroupRepository): GetGroupsUseCase =
        GetGroupsUseCaseImp(groupRepository)

    @Provides
    fun provideSubscribeToGroupUseCase(groupRepository: GroupRepository): SubscribeToGroupUseCase =
        SubscribeToGroupUseCaseImp(groupRepository)

    @Provides
    fun provideUnsubscribeToGroupUseCase(groupRepository: GroupRepository): UnsubscribeToGroupUseCase =
        UnsubscribeToGroupUseCaseImp(groupRepository)

    @Provides
    fun provideGetGroupsByTagUseCase(groupRepository: GroupRepository): GetGroupsByTagUseCase =
        GetGroupsByTagUseCaseImp(groupRepository)

    @Provides
    fun provideGetSubscribedGroups(groupRepository: GroupRepository): GetSubscribedGroupsUseCase =
        GetSubscribedGroupsUseCaseImp(groupRepository)

    @Provides
    fun provideGetUserInfo(userRepository: UserRepository): GetUserInfoUseCase =
        GetUserInfoUseCaseImp(userRepository)

    @Provides
    fun provideGetGroupsWithRole(groupRepository: GroupRepository): GetGroupsWithRoleUseCase =
        GetGroupsWithRoleUseCaseImp(groupRepository)

    @Provides
    fun provideCreateGroupUseCase(groupRepository: GroupRepository, storageRepository: StorageRepository): CreateGroupUseCase =
        CreateGroupUseCaseImp(groupRepository, storageRepository)

    @Provides
    fun provideCreatePostUseCase(postRepository: PostRepository, storageRepository: StorageRepository): CreatePostUseCase =
        CreatePostUseCaseImp(postRepository, storageRepository)

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

    @Provides
    fun provideGetPostsFromUserUseCase(postRepository: PostRepository): GetPostsFromUserUseCase =
        GetPostsFromUserUseCaseImp(postRepository)

    @Provides
    fun provideGetUpdatedPostsFromUserUseCase(postRepository: PostRepository): GetUpdatedPostsFromUserUseCase =
        GetUpdatedPostsFromUserUseCaseImp(postRepository)

    @Provides
    fun providesLogInUseCase(userRepository: UserRepository): LogInUserUseCase =
        LogInUserUseCaseImp(userRepository)

    @Provides
    fun providesRegisterUserUseCase(userRepository: UserRepository): RegisterUserUseCase =
        RegisterUserUseCaseImp(userRepository)

    @Provides
    fun providesLogOutUseCase(userRepository: UserRepository): LogOutUserUseCase =
        LogOutUserUseCaseImp(userRepository)
}