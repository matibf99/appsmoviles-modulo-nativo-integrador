package com.appsmoviles.gruposcomunitarios.utils.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

fun requestRequiredPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
    val pendingPermissions: ArrayList<String> = ArrayList()
    permissions.forEachIndexed { index, permission ->
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED)
            pendingPermissions.add(permission)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val array = arrayOfNulls<String>(pendingPermissions.size)
        pendingPermissions.toArray(array)
        requestPermissions(activity, array, requestCode)
    }
}