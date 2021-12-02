package com.appsmoviles.gruposcomunitarios.utils.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

private const val TAG = "PickImageIntent"

fun pickImageFromGalleryIntent(): Intent {
    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    return pickIntent
}

fun pickImageFromCameraIntent(applicationContext: Context): Intent {
    val photoURI = getImageUriTakenWithCamera(applicationContext, true)

    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    return takePhotoIntent
}

fun getImageUriTakenWithCamera(applicationContext: Context, delete: Boolean = false): Uri {
    val root = File(applicationContext.externalCacheDir, "my_images") // consider using getExternalFilesDir(Environment.DIRECTORY_PICTURES); you need to check the file_paths.xml
    if (!root.exists())
        root.mkdirs()

    val capturedPhoto = File(root, "image.jpeg")
    if (delete && capturedPhoto.exists())
        capturedPhoto.delete()

    return FileProvider.getUriForFile(
        applicationContext,
        applicationContext.packageName + ".fileprovider",
        capturedPhoto
    )
}