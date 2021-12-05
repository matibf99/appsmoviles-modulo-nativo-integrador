package com.appsmoviles.gruposcomunitarios.utils.notification

import android.content.Context
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object UpdatedPostHelper {
    private const val PREFERENCES_NOTIFICATIONS = "NOTIFICATIONS"
    private const val PREFERENCE_POSTS = "POSTS"

    fun getUpdatedPostList(context: Context): List<Post> {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NOTIFICATIONS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(PREFERENCE_POSTS, null)

        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Post>>(){}.type
            gson.fromJson(json, type)
        } else {
            arrayListOf()
        }
    }

    fun setUpdatedPostList(context: Context, list: List<Post>) {
        val edit = context.getSharedPreferences(PREFERENCES_NOTIFICATIONS, Context.MODE_PRIVATE).edit()

        val gson = Gson()
        val json = gson.toJson(list)
        edit.putString(PREFERENCE_POSTS, json)
        edit.apply()
    }

    fun removeOldPosts(context: Context) {
        val list = getUpdatedPostList(context).toMutableList()

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.MINUTE, -30)
        val date = cal.time

        val newList = ArrayList<Post>()
        for (item: Post in list) {
            if (item.lastCommentTimestamp!! >= date)
                newList.add(item)
        }

        setUpdatedPostList(context, newList)
    }
}