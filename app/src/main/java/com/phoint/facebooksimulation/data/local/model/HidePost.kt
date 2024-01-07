package com.phoint.facebooksimulation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HidePost {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "id_post")
    var idPost: Long? = null

    @ColumnInfo(name = "id_user_current")
    var idUserCurrent: Long? = null

    @ColumnInfo(name = "id_user_post")
    var idUserPost: Long? = null
}