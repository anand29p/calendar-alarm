package com.calendaralarm.app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility class for handling runtime permissions
 */
object PermissionUtils {
    
    /**
     * Check if all required permissions are granted
     */
    fun hasRequiredPermissions(context: Context): Boolean {
        // Calendar permission is required on all Android versions
        val hasCalendarPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        
        // Notification permission is required on Android 13+
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required on older versions
        }
        
        return hasCalendarPermission && hasNotificationPermission
    }
    
    /**
     * Get the list of required permissions based on Android version
     */
    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.READ_CALENDAR)
        
        // Add notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        return permissions.toTypedArray()
    }
    
    /**
     * Request required permissions
     */
    fun requestPermissions(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            getRequiredPermissions(),
            requestCode
        )
    }
    
    /**
     * Request required permissions using ActivityResultLauncher
     */
    fun requestPermissions(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher.launch(getRequiredPermissions())
    }
    
    /**
     * Check if we should show rationale for any permission
     */
    fun shouldShowRationale(activity: Activity): Boolean {
        return getRequiredPermissions().any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
    
    /**
     * Open app settings to allow the user to grant permissions manually
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}
