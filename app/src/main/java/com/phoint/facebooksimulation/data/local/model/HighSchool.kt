package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HighSchool {
    @PrimaryKey
    @ColumnInfo(name = "high_school_id")
    var highSchoolId: Long? = null

    @ColumnInfo(name = "name_high_school")
    var nameHighSchool: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}