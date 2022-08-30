package com.example.waterme.model

import com.google.firebase.database.Exclude

data class Plants(
    @get: Exclude
    var plantId : String? = null,

    var imageResources : String? = null,
    var plantTitle : String? = null,
    var plantDescription : String? = null,
    var plantReminder : String? = null
)