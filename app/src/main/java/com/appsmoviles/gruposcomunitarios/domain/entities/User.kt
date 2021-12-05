package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class User(
    val documentId: String? = null,
    val username: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val photo: String? = null
)