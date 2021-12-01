package com.appsmoviles.gruposcomunitarios.domain.entities

import java.util.*

data class Group(
    var documentId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val photo: String? = null,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val createdBy: String? = null,
    val tags: List<String>? = null,
    val moderators: List<String>? = null,
    var userRol: String? = null,
    var subscribed: Boolean? = false,
)