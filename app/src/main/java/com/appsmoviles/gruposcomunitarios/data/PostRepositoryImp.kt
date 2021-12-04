package com.appsmoviles.gruposcomunitarios.data

import android.util.Log
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.repository.PostRepository
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUPS_COLLECTION
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.GROUP_SUBCOLLECTION_POSTS
import com.appsmoviles.gruposcomunitarios.utils.FirestoreConstants.POST_SUBCOLLECTION_COMMENTS
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.SortBy
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
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
        awaitClose { channel.close() }
    }

    override fun modifyPost(groupId: String, postId: String, post: Post): Flow<Res<Nothing>> = callbackFlow {
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
        awaitClose { channel.close() }
    }

    override fun deletePost(groupId: String, postId: String): Flow<Res<Nothing>> = callbackFlow {
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
        awaitClose { channel.close() }
    }

    override fun getPost(groupId: String, postId: String): Flow<Res<Post>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .get()
            .addOnSuccessListener {
                val post = it.toObject(Post::class.java)

                if (post != null) {
                    post.documentId = it.id
                    trySend(Res.Success(post))
                }
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getPost: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override fun getPosts(groupId: String, sortBy: SortBy): Flow<Res<List<Post>>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .orderBy("createdAt", when(sortBy) {
                SortBy.CREATED_AT_ASCENDING -> Query.Direction.ASCENDING
                SortBy.CREATED_AT_DESCENDING -> Query.Direction.DESCENDING
                else -> Query.Direction.ASCENDING
            })
            .get()
            .addOnSuccessListener {
                val posts = ArrayList<Post>()

                for (documentSnapshot in it) {
                    val post = documentSnapshot.toObject(Post::class.java)
                    post.documentId = documentSnapshot.id

                    posts.add(post)
                }

                trySend(Res.Success(posts))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getPosts: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override fun getPostsFromAllGroups(sortBy: SortBy): Flow<Res<List<Post>>> = callbackFlow {
        db.collectionGroup(GROUP_SUBCOLLECTION_POSTS)
            .orderBy("createdAt", when(sortBy) {
                SortBy.CREATED_AT_ASCENDING -> Query.Direction.ASCENDING
                SortBy.CREATED_AT_DESCENDING -> Query.Direction.DESCENDING
                else -> Query.Direction.ASCENDING
            })
            .get()
            .addOnSuccessListener {
                val posts = ArrayList<Post>()

                for (documentSnapshot in it) {
                    val post = documentSnapshot.toObject(Post::class.java)
                    post.documentId = documentSnapshot.id

                    posts.add(post)
                }

                trySend(Res.Success(posts))
                Log.d(TAG, "getPostsFromAllGroups: success")
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getPostsFromAllGroups: error: ${it.message}")
            }
        awaitClose { channel.close() }
    }


    override fun getComments(groupId: String, postId: String): Flow<Res<List<PostComment>>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val comments = ArrayList<PostComment>()

                for (documentSnapshot in it) {
                    val comment = documentSnapshot.toObject(PostComment::class.java)
                    comment.documentId = documentSnapshot.id

                    comments.add(comment)
                }

                trySend(Res.Success(comments))
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "getComments: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override fun postComment(
        groupId: String,
        postId: String,
        comment: PostComment
    ): Flow<Res<Nothing>> = callbackFlow {
        val postRef = db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)

        db.runTransaction { transaction ->
            transaction.update(postRef, "commentsCount", FieldValue.increment(1))
            transaction.set(
                postRef.collection(POST_SUBCOLLECTION_COMMENTS).document(),
                comment
            )
            }
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "postComment: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override fun modifyComment(
        groupId: String,
        postId: String,
        commentId: String,
        comment: PostComment
    ): Flow<Res<Nothing>> = callbackFlow {
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
        awaitClose { channel.close() }
    }

    override fun likePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .update("likes", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                trySend(Res.Success())
                Log.d(TAG, "likePost: success")
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "likePost: error")
            }
        awaitClose { channel.close() }
    }

    override fun unlikePost(groupId: String, postId: String, userId: String): Flow<Res<Nothing>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .update("likes", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                trySend(Res.Success())
                Log.d(TAG, "unlikePost: success")
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "unlikePost: error")
            }
        awaitClose { channel.close() }
    }

    override fun likeComment(
        groupId: String,
        postId: String,
        commentId: String,
        username: String
    ): Flow<Res<Nothing>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .document(commentId)
            .update("likes", FieldValue.arrayUnion(username))
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "likeComment: error: ${it.message}")
            }
        awaitClose { channel.close() }
    }

    override fun unlikeComment(
        groupId: String,
        postId: String,
        commentId: String,
        username: String
    ): Flow<Res<Nothing>> = callbackFlow {
        db.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(GROUP_SUBCOLLECTION_POSTS)
            .document(postId)
            .collection(POST_SUBCOLLECTION_COMMENTS)
            .document(commentId)
            .update("likes", FieldValue.arrayRemove(username))
            .addOnSuccessListener {
                trySend(Res.Success())
            }
            .addOnFailureListener {
                trySend(Res.Error(it.message))
                Log.d(TAG, "unlikeComment: error: ${it.message}")
            }
        awaitClose { channel.close() }
    }
}