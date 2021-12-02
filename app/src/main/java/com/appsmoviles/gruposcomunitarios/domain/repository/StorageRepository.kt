package com.appsmoviles.gruposcomunitarios.domain.repository

import android.graphics.Bitmap
import com.appsmoviles.gruposcomunitarios.utils.Res
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    fun loadImageToStorage(userId: String, filename: String, bitmap: Bitmap): Flow<Res<String>>
}