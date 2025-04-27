package com.project.stageconnect.model

import androidx.lifecycle.ViewModel

class InternshipViewModel : ViewModel() {

    fun getOffers(): List<Internship> {
        return listOf(
            Internship("Développeur PHP", "NeuroByte", "Neurobyte est une société spécialisée dans le secteur de l’IA et de l’informatique quantique. Nous offrons des services pour accélérer la recherche et le développement de projets dans différents domaines comme les mathématiques, la physique ou encore la biologie.", "34000 Montpellier", "10 semaines"),
            Internship("UX Designer", "Creatix", "Refonte d'une interface mobile...", "75000 Paris", "10 semaines"),
            Internship("Data Analyst", "InData", "Analyse de données clients...", "13000 Marseille", "10 semaines")
        )
    }
}