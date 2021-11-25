package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class PostComment(
    val documentId: String? = null,
    val content: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val parent: String? = null,
)