package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Colleges {
    @PrimaryKey
    @ColumnInfo(name = "colleges_id")
    var collegesId: Long? = null

    @ColumnInfo(name = "name_colleges")
    var nameColleges: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}