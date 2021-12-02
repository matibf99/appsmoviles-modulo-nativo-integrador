package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.domain.repository.GroupRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUPS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.USERS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class GroupRepositoryImp(
    private val db: FirebaseFirestore
): GroupRepository {

    override suspend fun getGroups(sortBy: SortBy): Flow<Res<List<Group>>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .orderBy(when (sortBy) {
                SortBy.NAME_ASCENDING, SortBy.NAME_DESCENDING -> "name"
                SortBy.CREATED_AT_ASCENDING, SortBy.CREATED_AT_DESCENDING -> "createdAt"
                else -> "name"
            }, when(sortBy) {
                SortBy.NAME_ASCENDING, SortBy.CREATED_AT_ASCENDING -> Query.Direction.ASCENDING
                SortBy.NAME_DESCENDING, SortBy.CREATED_AT_DESCENDING -> Query.Direction.DESCENDING
                else -> Query.Direction.ASCENDING
            })
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getGroups: success!, size: ${it.size()}")
                val groups = ArrayList<Group>()

                for (documentSnapshot in it) {
                    val group = documentSnapshot.toObject(Group::class.java)
                    group.documentId = documentSnapshot.id

                    groups.add(group)
                }

                trySend(Res.Success(groups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getGroups: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun getGroupsByTag(tag: String): Flow<Res<List<Group>>> = callbackFlow {
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
                Log.d(TAG, "getGroupsByTag: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun getGroupInfo(groupId: String): Flow<Res<Group>> = callbackFlow {
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
                Log.d(TAG, "getGroupInfo: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun createGroup(group: Group): Flow<Res<Nothing>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(group.name!!)
            .set(group)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "createGroup: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun getSubscribedGroups(groupsIds: List<String>): Flow<Res<List<Group>>> = callbackFlow {
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
                        group.documentId = documentSnapshot.id
                        group.subscribed = true

                        subscribedGroups.add(group)
                    }
                }

                trySend(Res.Success(subscribedGroups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getSubscribedGroups: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun getGroupsWithRole(username: String): Flow<Res<List<Group>>> = callbackFlow {
        val task1 = db.collection(GROUPS_COLLECTION)
            .whereEqualTo("createdBy", username)
            .orderBy("name")
            .get()

        val task2 = db.collection(GROUPS_COLLECTION)
            .whereArrayContains("moderators", username)
            .orderBy("name")
            .get()

        Tasks.whenAllSuccess<QuerySnapshot>(task1, task2)
            .addOnSuccessListener {
                val groups = ArrayList<Group>()
                for (querySnapshot in it) {
                    for (documentSnapshot in querySnapshot) {
                        val group = documentSnapshot.toObject(Group::class.java)

                        if (groups.find { documentSnapshot.id == it.documentId } == null) {
                            group.documentId = documentSnapshot.id
                            group.userRol = if (username == group.createdBy) "Owner"
                                else "Moderator"

                            groups.add(group)
                        }
                    }
                }
                trySend(Res.Success(groups))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
            }
        awaitClose { channel.close() }
    }

    companion object {
        private const val TAG = "GroupRepositoryImp"
    }
}