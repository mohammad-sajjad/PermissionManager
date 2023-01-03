package com.vastpay.permissionmanager

import android.Manifest


/**
 * Created by Mohammad Sajjad on 03-01-2023.
 * Email mohammadsajjad679@gmail.com
 */

sealed class Permissions(vararg val permissions: String) {
    // Individual permissions
    object Camera : Permissions(Manifest.permission.CAMERA)

    // Bundled permissions
    object MandatoryForFeatureOne : Permissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Grouped permissions
    object Location : Permissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    object Storage : Permissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    object contacts: Permissions(
        Manifest.permission.READ_CONTACTS
    )


    companion object {
        fun from(permission: String) = when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> Location
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> Storage
            Manifest.permission.CAMERA -> Camera
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}