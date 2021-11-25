package com.appsmoviles.gruposcomunitarios.data

import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUPS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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

                for (documentSnapshot in it) {
                    val group = documentSnapshot.toObject(Group::class.java)
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

                for (documentSnapshot in it) {
                    val group = documentSnapshot.toObject(Group::class.java)
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

    override suspend fun getSubscribedGroups(groupsIds: List<String>): Flow<Res<List<Group>>> = callbackFlow {
        trySend(Res.Loading())

        val groupsIdsChunked = groupsIds.chunked(10)
        val tasks = ArrayList<Task<QuerySnapshot>>()

        for (ids in groupsIdsChunked) {
            val task = db.collection(GROUPS_COLLECTION)
                .whereIn(FieldPath.documentId(), ids)
                .get()

            tasks.add(task)
        }

        Tasks.whenAllSuccess<QuerySnapshot>(tasks)
            .addOnSuccessListener {
                val subscribedGroups = ArrayList<Group>()
                for (querySnapshotList in it) {
                    for (documentSnapshot in querySnapshotList) {
                        val group = documentSnapshot.toObject(Group::class.java)
                        subscribedGroups.add(group)
                    }
                }

                trySend(Res.Success(subscribedGroups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun subscribeToGroup(userId: String, groupId: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .update("groups", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

    override suspend fun unsubscribeToGroup(userId: String, groupId: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(userId)
            .update("groups", FieldValue.arrayRemove(groupId))
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
    }

}