package com.appcoins.diceroll.sdk.core.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionsManager @Inject constructor() {

    fun requestPermissions(activity: Activity) {
        val permissions = getPermissions()
        val notGrantedPermissions = arrayListOf<String>()

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED
            ) {
                notGrantedPermissions.add(permission)
            }
        }
        if (notGrantedPermissions.size > 0) {
            ActivityCompat.requestPermissions(
                activity, notGrantedPermissions.toTypedArray(), 1001
            )
        }
    }

    private fun getPermissions(): ArrayList<String> {
        val permissions = arrayListOf<String>()
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }
}
