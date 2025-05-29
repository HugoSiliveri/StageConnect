package com.project.stageconnect.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Classe utilitaire contenant des méthodes statiques.
 */
class Utils {
    companion object {

        /**
         * Extrait le code postal et la ville à partir d'une chaîne de localisation.
         *
         * @param location La chaîne de caractère comportant la localisation. La chaine doit contenir un code postal suivi d'une ville.
         *
         * @return Le code postal et la ville séparés par un espace.
         */
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

        /**
         * Extrait le nom du fichier à partir d'une URI.
         *
         * @param context Le contexte de l'application.
         * @param uri L'URI du fichier.
         *
         * @return Le nom du fichier avec son extension.
         */
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