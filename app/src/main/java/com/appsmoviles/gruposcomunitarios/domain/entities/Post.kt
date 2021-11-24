package com.appsmoviles.gruposcomunitarios.domain.entities

data class Post(
    val documentId: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val createdBy: String,
)