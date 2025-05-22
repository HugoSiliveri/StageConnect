package com.project.stageconnect.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

class Utils {
    companion object {

        fun extractPostalCodeAndCity(location: String): String {
            val regex = "(\\d{5})\\s([a-zA-ZÀ-ÿ\\s-]+)".toRegex()
            val matchResult = regex.find(location)

            return if (matchResult != null) {
                // matchResult.groupValues[1] = code postal, matchResult.groupValues[2] = ville
                "${matchResult.groupValues[1]} ${matchResult.groupValues[2]}"
            } else {
                "Localisation non valide"
            }
        }

        fun getFileNameFromUri(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            result = it.getString(index)
                        }
                    }
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result?.lastIndexOf('/')
                if (cut != null && cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result ?: "fichier inconnu"
        }
    }

}