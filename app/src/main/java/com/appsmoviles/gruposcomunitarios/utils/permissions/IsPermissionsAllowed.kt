package com.appsmoviles.gruposcomunitarios.utils.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun isPermissionsAllowed(activity: Activity, permissions: Array<String>, shouldRequestIfNotAllowed: Boolean = false, requestCode: Int = -1): Boolean {
    var isGranted = true

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        for (permission in permissions) {
            isGranted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
            if (!isGranted)
                break
        }
    }
    if (!isGranted && shouldRequestIfNotAllowed) {
        if (requestCode.equals(-1))
            throw RuntimeException("Send request code in third parameter")
        requestRequiredPermissions(activity, permissions, requestCode)
    }

    return isGranted
}