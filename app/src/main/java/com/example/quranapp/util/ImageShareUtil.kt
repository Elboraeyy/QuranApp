package com.example.quranapp.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ImageShareUtil {

    /**
     * Saves a bitmap to the cache directory and triggers an Intent to share it as an image.
     */
    fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            // Create a file in the cache directory
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val file = File(cachePath, "shared_hadith_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file) // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Get the URI from FileProvider
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                }
                context.startActivity(Intent.createChooser(shareIntent, "مشاركة كصورة"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
