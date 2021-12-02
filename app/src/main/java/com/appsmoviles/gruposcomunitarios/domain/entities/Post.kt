package com.appsmoviles.gruposcomunitarios.domain.entities

import com.google.firebase.firestore.Exclude
import java.util.*

data class Post(
    @get:Exclude val documentId: String? = null,
    val title: String? = null,
    val content: String? = null,
    val photo: String? = null,
    val commentsCount: Long? = null,
    val createdAt: Date? = null,
    val createdBy: String? = null,
)