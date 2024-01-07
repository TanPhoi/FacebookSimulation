package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Work {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "work_id")
    var workId: Long? = null

    @ColumnInfo(name = "work_user_id")
    var workUserId: Long? = null

    @ColumnInfo(name = "name_work")
    var nameWork: String? = null

    @ColumnInfo(name = "privacy")
    var privacy: String? = null
}