package com.appsmoviles.gruposcomunitarios.domain.entities

import com.google.firebase.firestore.Exclude
import java.util.*

data class PostComment(
    @get:Exclude var documentId: String? = null,
    val content: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val parent: String? = null,
)