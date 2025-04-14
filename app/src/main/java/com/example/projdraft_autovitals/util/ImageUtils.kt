package com.example.projdraft_autovitals.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

/**
 * Saves an image from the given [imageUri] to the app's internal storage directory.
 * Returns the absolute path of the saved image file or null if saving fails.
 */
fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
    return try {

        // Open input stream from the image URI
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)

        // Create a unique file name using the current timestamp
        val file = File(context.filesDir, "car_${System.currentTimeMillis()}.jpg")

        // Copy the input stream to the file output stream
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Return the full path of the saved file
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null  // Return null if any error occurs during saving
    }
}
