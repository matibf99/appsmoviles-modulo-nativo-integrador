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

private fun getRootFile(applicationContext: Context): File {
    val root = File(applicationContext.externalCacheDir, "my_images") // consider using getExternalFilesDir(Environment.DIRECTORY_PICTURES); you need to check the file_paths.xml
    if (!root.exists())
        root.mkdirs()

    return root
}

fun pickImageFromCameraIntent(activity: Activity): Intent {
    val applicationContext: Context = activity.applicationContext
    val root = getRootFile(applicationContext)

    val capturedPhoto = File(root, "image.jpeg")
    if (!capturedPhoto.exists()) {
        capturedPhoto.delete()
    }
    capturedPhoto.createNewFile()
    Log.d(TAG, "pickImageFromCameraIntent: ${capturedPhoto.absolutePath}")

    val photoURI = FileProvider.getUriForFile(
        applicationContext,
        applicationContext.packageName + ".fileprovider",
        capturedPhoto
    )

    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    //takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    //takePhotoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    return takePhotoIntent
}

fun getImageUriTaken(activity: Activity): Uri {
    val applicationContext: Context = activity.applicationContext
    val root = getRootFile(applicationContext)

    val capturedPhoto = File(root, "image.jpeg")
    return FileProvider.getUriForFile(
        applicationContext,
        applicationContext.packageName + ".fileprovider",
        capturedPhoto
    )
}