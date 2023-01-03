package com.vastpay.permissionmanager

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
 * Created by Mohammad Sajjad on 03-01-2023.
 * Email mohammadsajjad679@gmail.com
 */

class PermissionManager private constructor(private val activity: AppCompatActivity) {

    private val requiredPermissions = mutableListOf<Permissions>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permissions,Boolean>) -> Unit = {}

    private val permissionCheck = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        sendResultAndCleanUp(grantResults)
    }

//    private fun permissionCheck(): ActivityResultLauncher<Array<String>> {
//        return activity.reg
//    }

    companion object {

        fun from(activity: Activity) = PermissionManager(activity as AppCompatActivity)
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun request(vararg permission: Permissions): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkDetailedPermission(callback: (Map<Permissions,Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handlePermissionRequest() {
        activity.let { activity ->
            when {
                areAllPermissionsGranted(activity) -> sendPositiveResult()
                shouldShowPermissionRationale(activity) -> displayRationale(activity)
                else -> requestPermissions()
            }
        }
    }

    private fun displayRationale(Activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(Activity.getString(R.string.dialog_permission_title))
            .setMessage(rationale ?: Activity.getString(R.string.dialog_permission_default_message))
            .setCancelable(false)
            .setPositiveButton(Activity.getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissions()
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associate { it to true } )
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { Permissions.from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun areAllPermissionsGranted(activity: Activity) =
        requiredPermissions.all { it.isGranted(activity) }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun shouldShowPermissionRationale(activity: Activity) =
        requiredPermissions.any { it.requiresRationale(activity) }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    private fun Permissions.isGranted(activity: Activity) =
        permissions.all { hasPermission(activity, it) }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun Permissions.requiresRationale(activity: Activity) =
        permissions.any { activity.shouldShowRequestPermissionRationale(it) }

    private fun hasPermission(activity: Activity, permission: String) =
        ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}