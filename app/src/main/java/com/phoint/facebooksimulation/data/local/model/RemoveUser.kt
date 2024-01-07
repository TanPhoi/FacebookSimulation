package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RemoveUser {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    @ColumnInfo(name = "user_id_removed")
    var userIdRemoved: Long? = null

    @ColumnInfo(name = "user_id_is_removed")
    var userIdIsRemoved: Long? = null
}