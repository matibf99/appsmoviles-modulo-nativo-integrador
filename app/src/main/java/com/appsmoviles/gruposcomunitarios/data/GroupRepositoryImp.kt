package com.appsmoviles.gruposcomunitarios.data

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUPS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class GroupRepositoryImp(
    private val db: FirebaseFirestore
): GroupRepository {
    override suspend fun getGroups(sortBy: Int): Flow<Res<List<Group>>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .get()
            .addOnSuccessListener {
                val groups = ArrayList<Group>()

                for (querySnapshot in it) {
                    val group = querySnapshot.toObject(Group::class.java)
                    groups.add(group)
                }

                trySend(Res.Success(groups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun getGroupsByTag(tag: String): Flow<Res<List<Group>>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .whereArrayContains("tags", tag)
            .get()
            .addOnSuccessListener {
                val groups = ArrayList<Group>()

                for (querySnapshot in it) {
                    val group = querySnapshot.toObject(Group::class.java)
                    groups.add(group)
                }

                trySend(Res.Success(groups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun getGroupInfo(groupId: String): Flow<Res<Group>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .get()
            .addOnSuccessListener {
                val group = it.toObject(Group::class.java)

                if (group != null)
                    trySend(Res.Success(group))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun createGroup(group: Group): Flow<Res<Group>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document()
            .set(group)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun getSubscribedGroups(): Flow<Res<List<Group>>> {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToGroup(groupId: String): Flow<Res<Nothing>> {
        TODO("Not yet implemented")
    }

    override suspend fun unsubscribeToGroup(groupId: String): Flow<Res<Nothing>> {
        TODO("Not yet implemented")
    }

}