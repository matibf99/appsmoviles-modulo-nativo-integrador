package com.appsmoviles.gruposcomunitarios.domain.repository

import android.graphics.Bitmap
import com.appsmoviles.gruposcomunitarios.utils.helpers.Res
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun loadImageToStorage(root: String, filename: String, bitmap: Bitmap): Flow<Res<String>>
}