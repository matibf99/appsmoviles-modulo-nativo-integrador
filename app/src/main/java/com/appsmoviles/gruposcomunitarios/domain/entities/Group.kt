package com.appsmoviles.gruposcomunitarios.domain.entities

data class Group(
    val documentId: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val createdBy: String,
    val tags: List<String>,
)