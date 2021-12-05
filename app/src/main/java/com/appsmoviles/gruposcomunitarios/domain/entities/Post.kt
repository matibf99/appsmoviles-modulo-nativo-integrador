package com.appsmoviles.gruposcomunitarios.domain.entities

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Post(
    @get:Exclude var documentId: String? = null,
    val title: String? = null,
    val content: String? = null,
    val photo: String? = null,
    val commentsCount: Long? = null,
    val groupName: String? = null,
    val groupId: String? = null,
    val createdAt: Date? = null,
    val createdBy: String? = null,
    var likes: List<String>? = null,
    val latitude: String? = null,
    val longitude: String? = null
) : Parcelable