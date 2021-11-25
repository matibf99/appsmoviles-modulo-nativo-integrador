package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class Post(
    val documentId: String? = null,
    val title: String? = null,
    val content: String? = null,
    val commentsCount: Long? = null,
    val createdAt: Date? = null,
    val createdBy: Date? = null,
)