package com.appsmoviles.gruposcomunitarios.di

import android.content.Context
import android.content.SharedPreferences
import com.appsmoviles.gruposcomunitarios.data.GroupRepositoryImp
import com.appsmoviles.gruposcomunitarios.data.PostRepositoryImp
import com.appsmoviles.gruposcomunitarios.data.StorageRepositoryImp
import com.appsmoviles.gruposcomunitarios.data.UserRepositoryImp
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupsUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetGroupsUseCaseImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        val instance = FirebaseStorage.getInstance()
        instance.maxOperationRetryTimeMillis = 60000
        instance.maxDownloadRetryTimeMillis = 60000
        instance.maxUploadRetryTimeMillis = 60000

        return instance
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    @Provides
    fun provideGroupRepository(firestore: FirebaseFirestore): GroupRepository =
        GroupRepositoryImp(firestore)

    @Provides
    fun providePostRepository(firestore: FirebaseFirestore): PostRepository =
         PostRepositoryImp(firestore)

    @Provides
    fun provideUserRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): UserRepository =
        UserRepositoryImp(auth, firestore)

    @Provides
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository =
        StorageRepositoryImp(storage)
}