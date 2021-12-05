package com.appsmoviles.gruposcomunitarios.data

import android.graphics.Bitmap
import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.repository.StorageRepository
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.storage.resize
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.ByteArrayOutputStream

@ExperimentalCoroutinesApi
class StorageRepositoryImp(
    private val storage: FirebaseStorage
) : StorageRepository {
    override fun loadImageToStorage(root: String, filename: String, bitmap: Bitmap): Flow<Res<String>> = callbackFlow {
        val storageRef = storage.reference.child("$root/$filename")

        val resizedBitmap = resize(bitmap)
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
                Log.d(TAG, "loadImageToStorage: ")

                storageRef.downloadUrl.addOnSuccessListener {
                        trySend(Res.Success(it.toString()))
                    }
                    .addOnFailureListener {
                        trySend(Res.Error(it.message))
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "loadImageToStorage: failed")
                trySend(Res.Error(it.message))
            }
        awaitClose { channel.close() }
    }

    companion object {
        private const val TAG = "StorageRepositoryImp"
    }
}