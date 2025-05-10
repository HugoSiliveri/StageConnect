package com.project.stageconnect.utils

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
    }

}