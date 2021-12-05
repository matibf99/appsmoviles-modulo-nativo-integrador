package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.FirestoreConstants.USERS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class UserRepositoryImp(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val TAG = "UserRepositoryImp"
    }

    override suspend fun registerUserAuth(email: String, password: String): Flow<Res<Nothing>> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    trySend(Res.Success())
                }
                .addOnFailureListener {
                    trySend(Res.Error(it.message))
                    Log.d(TAG, "registerUser: ${it.message}")
                }
            awaitClose { channel.close() }
        }

    override suspend fun registerUserFirestore(user: User): Flow<Res<Nothing>> = callbackFlow {
        val documentId = getCurrentUserDocumentId()

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
            trySend(Res.Error("Document ID is empty"))
            Log.d(TAG, "registerUserFirestore: documentId is empty")
        }
        awaitClose { channel.close() }
    }

    override suspend fun logIn(email: String, password: String): Flow<Res<Nothing>> =
        callbackFlow {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    trySend(Res.Success())
                }
                .addOnFailureListener {
                    trySend(Res.Error(it.message))
                    Log.d(TAG, "signIn: ${it.message}")
                }
            awaitClose { channel.close() }
        }

    override suspend fun getCurrentUserInfo(): Flow<Res<User>> = callbackFlow {
        val documentId = getCurrentUserDocumentId()

        if (documentId != null) {
            db.collection(USERS_COLLECTION)
                .document(documentId)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)

                    if (user != null) {
                        trySend(Res.Success(user))
                    } else {
                        trySend(Res.Error("No user found"))
                    }
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

    override fun getCurrentUserDocumentId(): String? {
        return auth.currentUser?.email
    }

    override fun logOut() {
        auth.signOut()
    }
}