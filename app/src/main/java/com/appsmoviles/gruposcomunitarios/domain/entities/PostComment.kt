package com.appsmoviles.gruposcomunitarios.domain.entities

data class PostComment(
    val documentId: String,
    val content: String,
    val createdAt: String,
    val modifiedAt: String,
    val parent: String
)