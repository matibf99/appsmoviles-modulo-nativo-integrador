package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class Group(
    val documentId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val createdBy: String? = null,
    val tags: List<String>? = null,
)