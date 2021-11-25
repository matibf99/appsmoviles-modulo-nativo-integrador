package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUPS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUP_SUBCOLLECTION_POSTS
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.POST_SUBCOLLECTION_COMMENTS
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class PostRepositoryImp(
    private val db: FirebaseFirestore
) : PostRepository {

    companion object {
        private const val TAG = "PostRepositoryImp"
    }

    override fun createPost(groupId: String, post: Post): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document()
            .set(post)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "createPost: ${it.message}")
            }
    }

    override fun modifyPost(groupId: String, postId: String, post: Post): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .set(post)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "modifyPost: ${it.message}")
            }
    }

    override fun deletePost(groupId: String, postId: String): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .delete()
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "deletePost: ${it.message}")
            }
    }

    override fun getPost(groupId: String, postId: String): Flow<Res<Post>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .get()
            .addOnSuccessListener {
                val post = it.toObject(Post::class.java)
                trySend(Res.Success(post))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getPost: ${it.message}")
            }
    }

    override fun getPosts(groupId: String, sortBy: Int): Flow<Res<List<Post>>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .get()
            .addOnSuccessListener {
                val posts = ArrayList<Post>()

                for (documentSnapshot in it) {
                    val post = documentSnapshot.toObject(Post::class.java)
                    posts.add(post)
                }

                trySend(Res.Success(posts))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getPosts: ${it.message}")
            }
    }

    override fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .get()
            .addOnSuccessListener {
                val comments = ArrayList<PostComment>()

                for (documentSnapshot in it) {
                    val comment = documentSnapshot.toObject(PostComment::class.java)
                    comments.add(comment)
                }

                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getComments: ${it.message}")
            }
    }

    override fun postComment(
        groupId: String,
        postId: String,
        comment: PostComment
    ): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .document()
            .set(comment)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "postComment: ${it.message}")
            }
    }

    override fun modifyComment(
        groupId: String,
        postId: String,
        commentId: String,
        comment: PostComment
    ): Flow<Res<Nothing>> = callbackFlow {
        trySend(Res.Loading())

        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .document(commentId)
            .set(comment)
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "modifyComment: ${it.message}")
            }
    }
}