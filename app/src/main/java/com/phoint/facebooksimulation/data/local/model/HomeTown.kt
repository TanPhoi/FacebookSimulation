package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HomeTown {
    @PrimaryKey
    @ColumnInfo(name = "home_town_id")
    var homeTownId: Long? = null

    @ColumnInfo(name = "name_home_town")
    var nameHomeTown: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}