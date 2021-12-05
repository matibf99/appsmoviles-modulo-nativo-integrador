package com.appsmoviles.gruposcomunitarios.domain.entities

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Group(
    @get:Exclude var documentId: String? = null,
    val name: String? = null,
    val description: String? = null,
    var photo: String? = null,
    @ServerTimestamp val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val createdBy: String? = null,
    val tags: List<String>? = null,
    val moderators: List<String>? = null,
    var subscribed: List<String>? = null,
) : Parcelable