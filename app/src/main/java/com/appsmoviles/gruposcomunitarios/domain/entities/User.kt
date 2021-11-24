package com.appsmoviles.gruposcomunitarios.domain.entities

data class User(
    val documentId: String,
    val username: String,
    val name: String,
    val surname: String,
    val createdAt: String,
    val photo: String,
    val groups: List<String>,
)