package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.repository.UserRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.USERS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    override suspend fun registerUser(user: User, password: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        auth.createUserWithEmailAndPassword(user.email, password)
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
        trySend(Res.Loading())

        val documentId = auth.currentUser?.getIdToken(false)?.result?.token

        if (documentId != null) {
            db.collection(USERS_COLLECTION)
                .document(documentId)
                .get()
                .addOnSuccessListener {
                    trySend(Res.Success())
                }
                .addOnFailureListener {
                    trySend(Res.Error(it.message))
                    Log.d(TAG, "getCurrentUserInfo: ${it.message}")
                }
        } else {
            trySend(Res.Error())
            Log.d(TAG, "getCurrentUserInfo: can't obtain documentId")
        }
    }

}