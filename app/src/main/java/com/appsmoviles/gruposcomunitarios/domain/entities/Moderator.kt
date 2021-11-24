package com.appsmoviles.gruposcomunitarios.domain.entities

data class Moderator(
    val documentId: String,
    val username: String,
    val role: String,
    val addedAt: String,
    val modifiedAt: String,
)
