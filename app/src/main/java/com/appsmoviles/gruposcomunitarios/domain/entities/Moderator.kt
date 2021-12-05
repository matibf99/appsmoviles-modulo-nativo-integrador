package com.appsmoviles.gruposcomunitarios.domain.entities

import com.google.firebase.firestore.Exclude
import java.util.*

data class Moderator(
    @get:Exclude val documentId: String? = null,
    val username: String? = null,
    val role: String? = null,
    val addedAt: Date? = null,
    val modifiedAt: Date? = null,
)
