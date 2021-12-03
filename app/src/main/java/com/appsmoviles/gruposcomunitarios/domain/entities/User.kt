package com.appsmoviles.gruposcomunitarios.domain.entities

import com.google.firebase.firestore.Exclude
import java.util.*

data class User(
    @get:Exclude val documentId: String? = null,
    val username: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val photo: String? = null,
)