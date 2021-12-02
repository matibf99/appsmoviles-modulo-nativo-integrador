package com.appsmoviles.gruposcomunitarios.utils.permissions

import android.content.pm.PackageManager

fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
    var isGranted = true
    for (grantResult in grantResults) {
        isGranted = grantResult.equals(PackageManager.PERMISSION_GRANTED)
        if (!isGranted)
            break
    }
    return isGranted
}