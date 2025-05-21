package com.project.stageconnect.model

data class Application (
    var id: String = "",
    var userId: String = "",
    var internshipId: String = "",
    var status: String = "", // pending, accepted, denied
)

