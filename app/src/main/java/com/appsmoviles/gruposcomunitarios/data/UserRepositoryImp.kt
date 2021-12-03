package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.USERS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
class UserRepositoryImp(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val TAG = "UserRepositoryImp"
    }

    override suspend fun registerUser(user: User, password: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        auth.createUserWithEmailAndPassword(user.email!!, password)
            .addOnSuccessListener {
                val documentId = auth.currentUser?.getIdToken(false)?.result?.token

                if (documentId != null) {
                    db.collection(USERS_COLLECTION)
                        .document(documentId)
                        .set(user)
                        .addOnSuccessListener {
                            trySend(Res.Success())
                        }
                        .addOnFailureListener {
                            trySend(Res.Error(it.message))
                            Log.d(TAG, "registerUser: ${it.message}")
                        }
                } else {
                    trySend(Res.Error("Can't obtain the user data"))
                    Log.d(TAG, "registerUser: can't obtain documentId")
                }
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "registerUser: ${it.message}")
            }
    }

    override  fun registeredUser(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    override suspend fun signIn(email: String, password: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "signIn: ${it.message}")
            }
    }

    override suspend fun getCurrentUserInfo(): Flow<Res<User>> = callbackFlow {
        //val documentId = auth.currentUser?.getIdToken(false)?.result?.token
        val documentId = "nPOYu5Ad0Tj0WoZkN9qZ"

        if (documentId != null) {
            db.collection(USERS_COLLECTION)
                .document(documentId)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    trySend(Res.Success(user))
                }
                .addOnFailureListener {
                    trySend(Res.Error(it.message))
                    Log.d(TAG, "getCurrentUserInfo: ${it.message}")
                }
        } else {
            trySend(Res.Error())
            Log.d(TAG, "getCurrentUserInfo: can't obtain documentId")
        }
        awaitClose { channel.close() }
    }

    override suspend fun getCurrentUserDocumentId(): Flow<Res<String>> = flow {
        emit(Res.Success("nPOYu5Ad0Tj0WoZkN9qZ"))
    }

    override suspend fun subscribeToGroup(groupId: String): Flow<Res<Nothing>> = callbackFlow {
        val documentId = "nPOYu5Ad0Tj0WoZkN9qZ"

        db.collection(USERS_COLLECTION)
            .document(documentId)
            .update("groups", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                Log.d(TAG, "subscribeToGroup: success")
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "subscribeToGroup: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override suspend fun unsubscribeToGroup(groupId: String): Flow<Res<Nothing>> = callbackFlow {
        val documentId = "nPOYu5Ad0Tj0WoZkN9qZ"

        db.collection(USERS_COLLECTION)
            .document(documentId)
            .update("groups", FieldValue.arrayRemove(groupId))
            .addOnSuccessListener {
                Log.d(TAG, "unsubscribeToGroup: success")
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "unsubscribeToGroup: ${it.message}")
            }
        awaitClose { channel.close() }
    }
}