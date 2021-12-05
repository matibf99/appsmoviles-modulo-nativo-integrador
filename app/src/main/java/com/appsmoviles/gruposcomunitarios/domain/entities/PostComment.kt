package com.appsmoviles.gruposcomunitarios.domain.entities

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class PostComment(
    @get:Exclude var documentId: String? = null,
    val username: String? = null,
    val content: String? = null,
    @ServerTimestamp val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    var likes: List<String>? = null,
    val parent: String? = null,
) : Parcelable