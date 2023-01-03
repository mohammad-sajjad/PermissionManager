package com.vastpay.permissionmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissionManager = PermissionManager.from(this)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_location.setOnClickListener {
            permissionManager.request(Permissions.Location)
                .rationale("We require some permission")
                .checkPermission {
                    if (it) {
                        Toast.makeText(this, "Permission has been granted", Toast.LENGTH_SHORT).show()
                    } else {
                        // todo you can open setting
                        Toast.makeText(this, "required permission", Toast.LENGTH_SHORT).show()

                    }
                }
        }
        bnt_storage.setOnClickListener {
            permissionManager.request(Permissions.Storage)
                .rationale("We require some permission")
                .checkPermission {
                    if (it) {
                        Toast.makeText(this, "Permission has been granted", Toast.LENGTH_SHORT).show()
                    } else {
                        // todo you can open setting

                        Toast.makeText(this, "required permission", Toast.LENGTH_SHORT).show()

                    }
                }
        }

        
    }
}