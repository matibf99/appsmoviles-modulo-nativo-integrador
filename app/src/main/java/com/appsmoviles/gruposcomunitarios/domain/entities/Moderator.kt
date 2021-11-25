package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class Moderator(
    val documentId: String? = null,
    val username: String? = null,
    val role: String? = null,
    val addedAt: Date? = null,
    val modifiedAt: Date? = null,
)
