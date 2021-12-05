package com.appsmoviles.gruposcomunitarios.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appsmoviles.gruposcomunitarios.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.text.TextUtils

import android.os.Build
import android.provider.Settings.SettingNotFoundException
import com.appsmoviles.gruposcomunitarios.utils.permissions.PermissionsManager.isLocationEnabled


object PermissionsManager {
    const val LOCATION_PERMISSION_REQUEST_CODE = 0x0001

    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun requestLocationPermission(fragment: Fragment) {
        requestPermission(fragment, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
    }

    fun requestLocationPermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        if (isPermissionGranted(activity, permission).not()) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun showGPSNotEnabledDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.create_post_dialog_location_title)
            .setMessage(R.string.create_post_dialog_location_message)
            .setPositiveButton(R.string.create_post_dialog_location_positive_action) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.create_post_dialog_location_negative_action, null)
            .show()
    }
}