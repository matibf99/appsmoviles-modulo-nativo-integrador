package com.appsmoviles.gruposcomunitarios.worker

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.User
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetUpdatedPostsFromUserUseCase
import com.appsmoviles.gruposcomunitarios.domain.usecases.GetUserInfoUseCase
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import com.appsmoviles.gruposcomunitarios.utils.notification.NotificationUtils
import com.appsmoviles.gruposcomunitarios.utils.notification.UpdatedPostHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import java.util.*

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUpdatedPostsFromUserUseCase: GetUpdatedPostsFromUserUseCase
) : CoroutineWorker(context, params) {

    private var updatedPostList: MutableList<Post>? = null

    private var user: User? = null
    private var posts: List<Post>? = null
    private var num: Int = 0

    override suspend fun doWork(): Result {
        updatedPostList = UpdatedPostHelper.getUpdatedPostList(context).toMutableList()

        getUserInfoUseCase.getUserInfo().collect { resUser ->
            when (resUser) {
                is Res.Success -> {
                    user = resUser.data!!

                    getUpdatedPostsFromUserUseCase.getPosts(user!!.username ?: "", getRecentDate())
                        .collect { resPosts ->
                            when (resPosts) {
                                is Res.Success -> {
                                    posts = resPosts.data!!

                                    for (post: Post in posts!!) {
                                        if (updatedPostList?.any { it.documentId == post.documentId && it.lastCommentTimestamp == post.lastCommentTimestamp } == true)
                                            continue

                                        val pendingIntent = NavDeepLinkBuilder(context)
                                            .setComponentName(MainActivity::class.java)
                                            .setGraph(R.navigation.nav_graph_main)
                                            .setDestination(R.id.postFragment)
                                            .setArguments(bundleOf(Pair("post", post)))
                                            .createPendingIntent()

                                        NotificationUtils.sendNotification(
                                            context,
                                            post.title!!,
                                            context.getString(R.string.notification_new_comments_in_post),
                                            pendingIntent,
                                            num++
                                        )

                                        updatedPostList?.add(post)
                                    }

                                    UpdatedPostHelper.setUpdatedPostList(context, updatedPostList!!)
                                }
                                is Res.Loading -> Log.d(TAG, "doWork: loading posts")
                                is Res.Error -> Log.d(TAG, "doWork: error loading posts")
                            }
                        }
                }
                is Res.Loading -> Log.d(TAG, "doWork: loading user")
                is Res.Error -> Log.d(TAG, "doWork: error")
            }
        }

        return Result.success()
    }

    private fun getRecentDate(): Date {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.MINUTE, -15)
        return cal.time
    }

    companion object {
        private const val TAG = "NotificationWorker"
    }
}