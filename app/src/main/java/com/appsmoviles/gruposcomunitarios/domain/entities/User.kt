package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class User(
    val documentId: String,
    val username: String,
    val name: String,
    val surname: String,
    val email: String,
    val createdAt: Date,
    val modifiedAt: Date,
    val photo: String,
    val groups: List<String>,
)