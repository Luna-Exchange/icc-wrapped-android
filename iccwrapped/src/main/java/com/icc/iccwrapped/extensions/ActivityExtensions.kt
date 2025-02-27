package com.icc.iccwrapped.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Activity.openShareSheet(imageUri : Uri, message : String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        putExtra(Intent.EXTRA_TEXT, message)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun Activity.getImageUri(file: File): Uri {
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}