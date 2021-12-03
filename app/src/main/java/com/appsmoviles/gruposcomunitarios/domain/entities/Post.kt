package com.appsmoviles.gruposcomunitarios.domain.entities

import com.google.firebase.firestore.Exclude
import java.util.*

data class Post(
    @get:Exclude var documentId: String? = null,
    val title: String? = null,
    val content: String? = null,
    val photo: String? = null,
    val commentsCount: Long? = null,
    val groupId: String? = null,
    val createdAt: Date? = null,
    val createdBy: String? = null,
    var likes: List<String>? = null,
)